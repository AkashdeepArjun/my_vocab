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


                    //check if model have been downloaded

    private val _is_translator_available:MutableLiveData<ModelDownloadState> = MutableLiveData<ModelDownloadState>(ModelDownloadState.Loading())
    val is_translator_available:LiveData<ModelDownloadState> =_is_translator_available

    companion object{

        private val TAG="SHARED VIEW MODEL"

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


            // FETCHED VOCABS FROM ROOM DATABASE
             private val _fethed_vocab_state:MutableLiveData<UserProcessState> = MutableLiveData(UserProcessState.Loading("data is loading"))
                val fethed_vocab_state:LiveData<UserProcessState> = _fethed_vocab_state
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
             _fethed_vocab_state.postValue(UserProcessState.Success(it.size))
             Timber.tag(TAG).v("FETCH VOCABS SUCCESS!!\n ")
         }.onFailure {
             _fethed_vocab_state.postValue(UserProcessState.Error("error from viewmodel"))
             Timber.tag(TAG).e("FETCH VOCABS ERROR\n ${it.message}")
         }


     }




}

