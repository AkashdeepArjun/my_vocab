package com.example.my_vocab.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.my_vocab.*
import com.example.my_vocab.data.datamodel.Vocab
import com.example.my_vocab.repo.VocabRepo
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.sdkinternal.model.RemoteModelDownloadManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.Closeable
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(val application: Application, val repo: VocabRepo):ViewModel() {

    private var conditions: DownloadConditions?=null
    var eng_to_hindi_translator: Translator?=null
    private var options: TranslatorOptions?=null
    private var task: Task<Text>? =null
    var finished_detecting_texts=false
    private var timer_map=HashMap<String,Int>()

    //          QUIZ SCORE SET UP

            var correct_answers=0
            var wrong_answers=0
            var unattempted_answers=0
            var score:Float= 0.0f
            var attempted=false

            fun setAttempted(){
                attempted=true
            }

            fun unsetAttempted(){

                attempted=false
            }
            fun didCorrect(){
                correct_answers+=1
                score=score+total_score()
            }

            fun didWrong(){
                wrong_answers+=1
                score+total_score()
            }

            fun didNotAttempt(){
                unattempted_answers+=1
                score+=total_score()
            }

            fun total_score():Float{
                val score= ((correct_answer_point*correct_answers)+(wrong_answers* wrong_answer_point)+(unattempted_answer_point*unattempted_answers)).toFloat()
                return score
            }
                    //CHECK IF MODEL HAVE BEEN DOWNLOADED

    private val _is_translator_available:MutableLiveData<ModelDownloadState> = MutableLiveData<ModelDownloadState>(ModelDownloadState.Loading())
    val is_translator_available:LiveData<ModelDownloadState> =_is_translator_available

    companion object{

        private val TAG="SHARED VIEW MODEL"

        val correct_answer_point=1
        val wrong_answer_point=-1
        val unattempted_answer_point=-0.5

    }


            // SELECTED WORDS THAT USER WANTS TO TRANSLATE AFTER TEXT DETECTION
    var selected_words:MutableList<String> = mutableListOf<String>()


                // TO AVOID DETECTION FUNCTION CALLS MORE THAN ONCE IN FUTURE I WILL REMOVE THAT CRAPY LOGIC (-_-)
    var detection_count=0


                    // INPUT IMAGE FOR MACHINE LEARNIING KIT
    private var input_image:InputImage? = null

                        // INPUT IMAGE URI FOR DETECTING TEXTS
    private var uri:Uri?=null


                        // DETECTS WHETHER DETECTION WAS SUCCCESSFUL

    private val _detection_state=TextDetectionState.Loading("loading")

                        // TEXT RECOGNIZER/TEXT DETECTOR
    private var text_recognizer: TextRecognizer?=null

                        //DETECTED TEXTS STORED IN LIST
     val detected_texts:MutableList<String> = mutableListOf<String>()


                        //TRANSLATED_TEXT STATE  INDICATES WHETHER TRANSLATION WAS SUCCESS

    private val _translation_state:MutableLiveData<TranslationState> = MutableLiveData<TranslationState>(TranslationState.Loading())
            val translation_state:LiveData<TranslationState> = _translation_state


                        // TEXT DETECTION STATUS LIVE DATA INDICATES WHETHER TEXT WAS READ SUCCESSFULLY OR NOT

    private val _textReadState:MutableLiveData<TextDetectionState> = MutableLiveData<TextDetectionState>(TextDetectionState.Loading(""))
    val textReadState:LiveData<TextDetectionState> = _textReadState


                        //TRANSLATE MULTIPLE WORDS STATUS

        private val _translate_status:MutableLiveData<TranslateMultipleWordsStatus> = MutableLiveData(TranslateMultipleWordsStatus.Loading())
            val translate_status:LiveData<TranslateMultipleWordsStatus>  = _translate_status

                    //TRANSLATED WORDS STORED IN MAP  IN WAY SUCH THAT KEY=WORD ,VALUE=TRANSLATED WORD

    var translated_words:MutableMap<String,String> = mutableMapOf<String,String>()


            // STATUS OF SAVING WORDS TO ROOM DATABASE DICTIONARY
    private val _saving_words_status:MutableLiveData<UserProcessState> = MutableLiveData(UserProcessState.Loading("loadding"))
    val saving_words_status:LiveData<UserProcessState> = _saving_words_status


            // FETCHED VOCAb STATE FROM ROOM DATABASE
             private val _fethed_vocab_state:MutableLiveData<UserProcessState> = MutableLiveData(UserProcessState.Loading("data is loading"))
                val fethed_vocab_state:LiveData<UserProcessState> = _fethed_vocab_state

            //FETCHED VOCABS
            val fetched_vocabs=HashMap<String,String>()


            //TIMER FOR GAME
            private val _clock_timer:MutableLiveData<TimerState> = MutableLiveData<TimerState>(TimerState.INITIALIZE(null))
            val clock_timer:LiveData<TimerState>  = _clock_timer

    init {
        Timber.tag("VIEWMODEL").v("viewmodel instance created with hashcode ${this.hashCode()}")
        Timber.tag("VIEWMODEL").v("application instance created with hashcode ${application.hashCode()}")
        Timber.tag("VIEWMODEL").v("repo instance created with hashcode ${repo.hashCode()}")


        init_textRecognizer()               //initialize text recognizer
        setupTranslator()               // setting up and initialize the google translator
     download_translator_model()   // downloads translator model
//        translateWords()           // translates the selected words
        Log.e("VIEWMODEL","initialized")

//        getAllVocabs()

    }


                // CLOSES THE TRANSLATOR OBJECT WHEN IT IS NOT REQUIRED




                // PHOTO URI  SETTING UTILITY
                fun setUpPhotoUri(uri: Uri?){
                    this.uri=uri

                 }

            //  PREPARE IMAGE FOR DETECTING TEXTS VIA  GOOGLE ML KIT LIBRARY
            fun prepareInputImage(){
                try{

                    this.input_image=InputImage.fromFilePath(application.baseContext,this.uri!!)

                }catch (exception:Exception){
                    Log.wtf(TAG,"INPUT IMAGE PREPARATION FAILED :${exception.message}")
                }
            }


                        // SETTING UP TEXT RECOGNIZER CALLED IN CONSTRUCTOR OF VIEWMODEL
    fun init_textRecognizer(){
        text_recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }


                    //DETECTS TEXT IN A PHOTO URI

     fun detect_texts(uri: Uri) {

         viewModelScope.launch {


         _textReadState.postValue(TextDetectionState.Loading("texts being detected!!"))
          task=text_recognizer!!.process(input_image!!)

             .addOnSuccessListener {vision_texts->

                 for(block in vision_texts.textBlocks){

                     for(line in block.lines){
                         for(element in line.elements){
                             if(!detected_texts.contains(element.text)){
                                 detected_texts.add(element.text)

                             }
                         }

//                        detected_texts.add(line.toString())
                     }

                 }
//            result=TextDetectionState.Successs(detected_texts)

             }
             .addOnFailureListener {
                 _textReadState.postValue(TextDetectionState.Error(Exception(it)))
                 finished_detecting_texts=false
             }
             .addOnSuccessListener {
                 _textReadState.postValue(TextDetectionState.Successs(detected_texts))

                 detection_count+=1
                 finished_detecting_texts=true

             }

     }


     }




    override fun onCleared() {
        super.onCleared()
//        detected_texts.removeAll { 1>0 }
        resetEverything()
        Toast.makeText(application.baseContext,"viewmodel cleared",Toast.LENGTH_SHORT).show()
        closeTranslater()
    }



                                // RESETS DETECTED TEXTS  IT IS CALLED WHEN TEXT DETECTION FRAGMENT IS DESTROYEDD

    fun resetEverything(){
        if(!detected_texts.isEmpty()){

            detected_texts.removeAll { true }

        }


        }


                    //RESETS SELECTED WORDS  CALLED WHEN IT IS NO LONGER NEEDED IN FRAGMENTS

    fun resetSelectedWords(){
        if(selected_words.isNotEmpty()){
            selected_words.removeAll { true }
        }
    }

                // SETTING UP TRANSLATER .IT IS INITIALIZED WHEN CONSTRUCTOR OF VIEWMODEL CALLED

    fun setupTranslator(){

         options=TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI)
            .build()

        eng_to_hindi_translator= Translation.getClient(options!!)


    }

                        //DOWNLOADS TRANSLATOR MODEL
    fun download_translator_model()=viewModelScope.launch(Dispatchers.IO){

        _is_translator_available.postValue(ModelDownloadState.Loading())
        conditions=DownloadConditions
            .Builder()
            .requireWifi()
            .build()
        eng_to_hindi_translator!!
            .downloadModelIfNeeded(conditions!!)
            .addOnSuccessListener {
                _is_translator_available.postValue(ModelDownloadState.Successs())
            }
            .addOnFailureListener {
            _is_translator_available.postValue(ModelDownloadState.Error(it))

            }

        
    }

    fun resetTranslatedWords(){
        if(translated_words.size>0){

            translated_words.clear()
        }
    }

                // REMOVES PARTICULAR WORD FROM TRANSLATED WORDDS

    fun removeTranslatedWord(word: String){
        translated_words.remove(word)
    }



                // CLOSES THE TRANSLATOR
    fun closeTranslater(){
        eng_to_hindi_translator!!.close()
    }


                  //TRANSLATES SELECTED WORDS

     fun translateSelectedWords()=viewModelScope.launch(Dispatchers.IO) {

        _translate_status.postValue(TranslateMultipleWordsStatus.Loading())

        if(selected_words.isEmpty()){
            _translate_status.postValue(TranslateMultipleWordsStatus.Error("selected word is empty"))
        }
        Log.e("VIEWMODEL","selected words are ${selected_words}")
         var target_string=selected_words.joinToString(separator = ", ", prefix = "", postfix = ""){it->it }
         eng_to_hindi_translator!!.translate(target_string).addOnSuccessListener {
             translated_string->
             val list=translated_string.split(",").toMutableList()
             Log.e("VIEWMODEL ","list is ${list}")
             var index=0
             while(index<list.size){
                 translated_words.put(selected_words[index],list[index])
                 index++
             }
             list.clear()

            _translate_status.postValue(TranslateMultipleWordsStatus.Success())
         }.addOnFailureListener { _translate_status.postValue(TranslateMultipleWordsStatus.Error("wtf happened")) }


    }


    fun onUserSelectedWordsSuccesfully()=viewModelScope.launch{


            translateSelectedWords()
    }

    fun save_words_to_dictionary()=viewModelScope.launch(Dispatchers.IO) {
            _saving_words_status.postValue(UserProcessState.Loading("loading..."))
        var words= mutableListOf<Vocab>()
        for(word in translated_words){
            val vocab=Vocab(word=word.key, meaning = word.value,)
            words.add(vocab)
        }
        kotlin.runCatching {
            repo.insert(words)
        }
            .onSuccess { _saving_words_status.postValue(UserProcessState.Success(words.size))
            words.clear()
            }
            .onFailure {
                _saving_words_status.postValue(UserProcessState.Error("could not save words due to reason : ${it.message}"))
                words.clear()
            }

    }

     fun getAllVocabs()=viewModelScope.launch(Dispatchers.IO) {
        _fethed_vocab_state.postValue(UserProcessState.Loading("loading data....."))
         kotlin.runCatching {
             repo.getAllVocabs()
         }.onSuccess {
             list->
             for(i in list){
                 fetched_vocabs.put(i.word,i.meaning)
             }
             Timber.tag(TAG).v("FETCH VOCABS SUCCESS!!\n ")
             _fethed_vocab_state.postValue(UserProcessState.Success(list.size))

         }.onFailure {
             _fethed_vocab_state.postValue(UserProcessState.Error("error from viewmodel"))
             Timber.tag(TAG).e("FETCH VOCABS ERROR\n ${it.message}")
         }


     }

    fun startTimer(time_in_seconds:Int,delay:Long?=null)=viewModelScope.launch(Dispatchers.IO){
        var second_count=time_in_seconds
        _clock_timer.postValue(TimerState.STARTTED("started"))

        kotlin.runCatching {
            if(delay!=null){
                delay(delay/1000L)
            }
            while (second_count!=0){
                delay(1000L)
                _clock_timer.postValue(TimerState.RUNNING("${second_count}"));
                second_count-=1
            }
        }.onSuccess {
            _clock_timer.postValue(TimerState.DONE("done"))

        }.onFailure {
            _clock_timer.postValue(TimerState.Error("failed"))
        }

    }

    fun mytimer(time_in_seconds: Int,delay_in_milliseconds: Long,count:Int)= flow {

            repeat(count){

                (time_in_seconds..0).forEach{
                    delay(1000L)
                    emit(it)

                }

            }





        }.flowOn(Dispatchers.Main)


    fun resetQuizData(){
        score=0.0f
        unattempted_answers=0
        correct_answers=0
        wrong_answers=0
        attempted=false
    }

}

