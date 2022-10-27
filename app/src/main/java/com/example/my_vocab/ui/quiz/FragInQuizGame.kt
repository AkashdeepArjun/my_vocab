package com.example.my_vocab.ui.quiz

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.my_vocab.R
import com.example.my_vocab.TimerState
import com.example.my_vocab.data.datamodel.Vocab
import com.example.my_vocab.databinding.FragInQuizGameBinding
import com.example.my_vocab.observeOnce
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timerTask
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlin.random.nextInt

@AndroidEntryPoint
class FragInQuizGame :Fragment() {

    private var questions:List<Vocab> ? =null
    private var user_given_correct_answer=false
    private var chip: Chip?=null
    private var selected_chip:Chip?=null
    private var random_word_meaning: String?=null
    private var random_word: String?=null
    private var right_chip_id: Int?=null
    private  var clock_timer: ReceiveChannel<Unit>? =null
    private var timer:CountDownTimer?=null
    private var counting=1;
    private var should_cancel_dialog=false
    private lateinit var binding:FragInQuizGameBinding
    private  var on_back_press_call_backs:OnBackPressedCallback?=null
    val args:FragInQuizGameArgs by navArgs()
    private val viewmodel:SharedViewModel by activityViewModels()

            //QUESTION COUNTER
    private var question_counter=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater= TransitionInflater.from(requireContext())
        exitTransition=inflater.inflateTransition(R.transition.slide_from_right)
        enterTransition=inflater.inflateTransition(R.transition.slide_from_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragInQuizGameBinding.inflate(inflater)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.resetQuizData()
//        val args=FragInQuizGameArgs by navArgs<Int>()
        initThings()
        setupQuestions()

 }


                        //enable bottom nav when in game




    fun init_title(){

        val title:String=when(args.testType){
            2->{"progressor test"}
            3->{"scholar test"}
            else->{"rapid test"}
        }

        binding.tvTypeOfTest.text=title
        binding.cvMeanings.isSelectionRequired=true
        binding.cvMeanings.isSingleSelection=true

    }

                     //initialize stuff
    fun initThings(){
        on_back_press_call_backs=object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
               val dialog= showQuitDialog("quit the quiz?")

            }
        }
    }

    fun showQuitDialog(message:String): AlertDialog? {
        return MaterialAlertDialogBuilder(context!!)
            .setTitle(message)
            .setPositiveButton("yes"
            ) { _, _ ->
                this@FragInQuizGame.findNavController().popBackStack(R.id.frag_quiz, false)
            }
            .setNegativeButton("no"
            ) { _, _ -> should_cancel_dialog = true }
            .setCancelable(false)
            .show()
    }


        fun setUpListeners(){

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,on_back_press_call_backs!!)

        }


    override fun onDestroy() {
        super.onDestroy()
//        enableBottomNav()
        viewmodel.resetQuizData()

    }

    fun setupQuestions(){
        viewmodel.all_vocabs.observe(viewLifecycleOwner,Observer{
            list->
            if(list.isNotEmpty()){
                questions=list
                binding.quesLoadingAnim.cancelAnimation()
                binding.questionsAvailableLayout.visibility=View.GONE
                initTimer()
                init_title()
                setUpListeners()
                setupTest()
            }

        })

    }


                    //   QUIZ LOGIC

    fun logicQuiz(){

        // MAKES SURE OLD QUESTION AND ANSWERS IS WIPED OUT BEFORE NEW QUESTION APPEARS
            removeAllChips(binding!!.cvWord)
            removeAllChips(binding!!.cvMeanings)


                     // RANDOM WORD PICKED
            val vocab=questions!!.random()
            random_word=vocab.word
            random_word_meaning=vocab.meaning
//            val index_of_random_word=viewmodel.fetched_vocabs.keys.indexOf(random_word)

                //  MULTIPLE OPTIONS MEANINGS ONLY ONE IS CORRECT
            val words= questions!!.shuffled().take(9).takeWhile { it.meaning!=random_word_meaning }
            val meanings:List<String> =words.map {
                word->word.meaning
            }

                //      ADDS QUESTION CHIP
            val chip_id:Int?=addSingleChip(random_word.toString(), binding.cvWord)

            val word_chip= chip_id?.let { binding.cvWord.findViewById<Chip>(it) }

            val chip_default_bg=word_chip!!.chipBackgroundColor!!

            val chip_default_text_color= word_chip.textColors

                 //  ADDS OPTIONS CHIPS
            addTextstoChip(meanings, binding.cvMeanings)

                //ADDS REAL ANSWER CHIP AMONG OPTION CHIPS AT RANDOM POSITION
             right_chip_id=addChipAtIndex(random_word_meaning!!,Random.nextInt(0, binding.cvMeanings.childCount+1),
                 binding.cvMeanings)

                // LISTENS TO CHECK STATES ON OPTIONS
        binding!!.cvMeanings.forEach {
                view  ->

            (view as Chip).setOnCheckedChangeListener { buttonView, isChecked ->

                if(buttonView.isChecked){
                    viewmodel.setAttempted()
                    if(random_word_meaning.toString() == view.text.toString())
                    {
                        ( buttonView as Chip).setChipBackgroundColorResource(R.color.bilkul_light_green)
                        word_chip.setChipBackgroundColorResource(R.color.bilkul_light_green)
                        viewmodel.didCorrect()
                        binding.correct.text=viewmodel.correct_answers.toString()
                        disableChipsCheckListener(binding.cvMeanings)

                    }
                    else{

                        ( buttonView as Chip).setChipBackgroundColorResource(R.color.wrong_chip_color)
                        (buttonView as Chip).setTextColor(resources.getColor(R.color.white))
                        user_given_correct_answer=false
                        viewmodel.didWrong()
                        binding.wrong.text=viewmodel.wrong_answers.toString()
                        disableChipsCheckListener(binding.cvMeanings)

                    }
//                       disableChipsCheckListener(binding!!.cvMeanings)
                }else{
                    ( buttonView as Chip).chipBackgroundColor=chip_default_bg
                    ( buttonView as Chip).setTextColor(chip_default_text_color)
                    word_chip.chipBackgroundColor=chip_default_bg
                    viewmodel.unsetAttempted()

                }


            }
        }

    }

    fun disableChipsCheckListener(group: ChipGroup){

            group.forEach { v->(v as Chip).isCheckable=false}

    }

    fun is_meaning_correct(word:String,selected_meaning:String):Boolean=viewmodel.fetched_vocabs.get(word)==selected_meaning

                    //ADDS SINGLE CHIP IN A GROUP
    fun addSingleChip(itext:String,chipGroup: ChipGroup?):Int?{
        var chip_id: Int? =null
        if(chipGroup!=null){

            val chip= Chip(this.context)
            chip.text=itext
            chip.isCloseIconVisible=false
            chipGroup.addView(chip as View)
            chip_id=chip.id

        }

        return  chip_id
//        chip.setOnCheckedChangeListener { buttonView, isChecked ->  }
    }


                //ADDS MUTLIPLE CHIPS IN GROUP
    fun addTextstoChip(list: List<String>,group: ChipGroup?) {

        if(group!=null){
            for (text in list) {
                val chip = Chip(this.context)
                chip.text = text
                chip.tag=text
                chip.isCloseIconVisible = false
                chip.isCheckable = true
                group.addView(chip as View)

            }
        }



    }
            //REMOVE ALL CHIPS FROM CHIPGROUP
    fun removeAllChips(group: ChipGroup){

        if(group.childCount>0){
            group.removeAllViews()
        }
    }


            //RETURNS SPECIFIC CHIP FROM CHIPGROUP

    fun findChipById(id:Int,chipGroup: ChipGroup):Chip{
        val chip:Chip=chipGroup.findViewById<Chip>(id)
        return chip
    }


             // ADDS NEW CHIP WITH SPECIFIED TEXT AT SPECIC INDEX IN GROUP

    fun  addChipAtIndex(chip_text:String,index:Int,chipGroup: ChipGroup?):Int?{
        var id:Int?=null
        if(chipGroup!=null){

            val chip= Chip(this.context)
            chip.text=chip_text
            chip.isCloseIconVisible = false
            chip.isCheckable = true
            chipGroup.addView(chip,index)
            id=chip.id

        }

        return id
    }




            //STARTS QUIZ INPUT THE NUMBER OF GAMES

     @OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class,
         InternalCoroutinesApi::class
     )

                // INITIALIZE THE SECOND CLOCK TIMER
     fun initTimer(){
         clock_timer=ticker(1000L,0L,lifecycleScope.coroutineContext,TickerMode.FIXED_PERIOD)


     }


     @OptIn(ObsoleteCoroutinesApi::class)
     fun startQuiz(number_of_words: Int,time_in_seconds:Int) =scopemain@lifecycleScope.launch{
//         val range=30000L..300000L

         binding.tvWordsCount.text = "1/${number_of_words}"
         logicQuiz()

         var while_loop_counter = 1;
//         val ticker_channel=ticker(30000L,30000L,lifecycleScope.coroutineContext,TickerMode.FIXED_PERIOD)

         while(while_loop_counter<=number_of_words){
                 val right_chip = binding!!.cvMeanings.findViewById<Chip>(right_chip_id!!)

                 var clock_sec = 0
                 //question updates every 30 seconds
//
             val timer_job=launch {

                        withTimeoutOrNull(time_in_seconds*1000L) {

                            clock_timer!!.receiveAsFlow().take(time_in_seconds).cancellable().collect {

                                clock_sec += 1

                                binding.tvClock.text = "${time_in_seconds - clock_sec}"



                                if (clock_sec == time_in_seconds ) {

                                    if(!viewmodel.attempted){
                                        viewmodel.didNotAttempt()
                                        binding!!.unattempted.text=viewmodel.unattempted_answers.toString()

                                    }
                                    viewmodel.unsetAttempted()
                                    if(while_loop_counter<number_of_words){
                                        question_counter += 1
                                        binding.tvWordsCount.text =
                                            "${question_counter}/${number_of_words}"
                                        logicQuiz()
//                                    user_checked_atleast_once=false
                                        user_given_correct_answer=false


                                    }else{
//                                        findNavController().popBackStack(R.id.frag_home,false)
//                                        this@FragInQuizGame.findNavController().navigate(FragInQuizGameDirections.actionFragInQuizGameToFragScore(viewmodel.score))
                                        // logic to navigate to winner/looser frag
//                                        val result:Float=(viewmodel.correct_answers/number_of_words).toFloat()
                                        findNavController().navigate(FragInQuizGameDirections.actionFragInQuizGameToFragMatchResult(viewmodel.score))

                                    }




                                }

                            }

                        }
                    }
                    timer_job.join()
//                    if(timer_job.isCancelled){
//                        continue
//                    }

                if(timer_job.isCancelled){
                    while_loop_counter+=1
                    continue
                }

                            // countdown timer logic


                 while_loop_counter+=1
             }

         }




//         fun resetQuestionCounter() {
//             question_counter = 1
//         }

//         fun subToData() {
//
//             viewmodel.clock_timer.observe(viewLifecycleOwner, Observer { state ->
//                 if (state is TimerState.DONE) {
//                     logicQuiz()
//                     viewmodel.clock_timer.removeObservers(this)
//                 }
//                 if (state is TimerState.RUNNING) {
//                     binding!!.tvClock.text = state.message
//                 }
//             })
//
//         }


         fun setupTest() {
             when (args.testType) {
                 1 -> {
                    viewmodel.current_type="Rapid Test"
                     viewmodel.total_questions=10
                     startQuiz(10,10)

                 }
                 2 -> {
//                binding!!.tvWordsCount.text="1/${((viewmodel).fetched_vocabs.size/2)}"
                     viewmodel.current_type="Progressor Test"
                     viewmodel.total_questions=questions!!.size/2
                     startQuiz(questions!!.size/2,10)

                 }
                 else -> {
//                binding!!.tvWordsCount.text="1/${viewmodel.fetched_vocabs.size}"
                     viewmodel.current_type="Scholar Test"
                     viewmodel.total_questions=questions!!.size
                     startQuiz(questions!!.size,10)
                 }
             }
         }




}












