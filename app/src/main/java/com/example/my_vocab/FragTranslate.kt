package com.example.my_vocab

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.view.allViews
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.my_vocab.databinding.FragTranslateBinding
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FragTranslate:Fragment() {

    var snackbar:Snackbar?=null
    private val vm: SharedViewModel by activityViewModels()
    private  var binding:FragTranslateBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragTranslateBinding.inflate(inflater)
//        return super.onCreateView(inflater, container, savedInstanceState)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSelectedWords()
        vm.onUserSelectedWordsSuccesfully()
        addEventObservers()
        addSubs()
        setUpListeners()
        Timber.tag("FRAGMENT TRANSLATE").v("fragment translate created with hashcode ${vm.hashCode()}")


    }

//    override fun onResume() {
//        super.onResume()
//        Toast.makeText(context,"fragment resumed",Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Toast.makeText(context,"fragment stopped!!",Toast.LENGTH_SHORT).show()
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Toast.makeText(context,"fragment destroyed!!",Toast.LENGTH_SHORT).show()
//
//    }


    private fun addEventObservers(){
        this.lifecycle.addObserver(FragsLifeCycleObserver(vm))
//        this.lifecycle.addObserver(vm.eng_to_hindi_translator!!)
    }

    private fun initializeSelectedWords(){

        if(vm.selected_words.isNotEmpty()){
            addTextstoChip(vm.selected_words,binding!!.cgSelectedWords)
            binding!!.emptySelectedWordsMsg.visibility=View.GONE

        }
        else{
            binding!!.emptySelectedWordsMsg.visibility=View.VISIBLE
        }


    }

    fun addTextstoChip(list: MutableList<String>,chipGroup: ChipGroup) {

        for(word in list){
            val chip = Chip(this.context)
            chip.text = word
            chip.isCloseIconVisible = false
            chip.isCheckable = true
            chip.tag=chip.text
            chipGroup.addView(chip as View)
        }
    }


    fun addSingleChip(id:Int,text:String,chipGroup: ChipGroup){

        val chip=Chip(this.context)
        chip.id=id
        chip.text=text
        chip.isCloseIconVisible=true
        chipGroup.addView(chip as View)


//        chip.setOnCheckedChangeListener { buttonView, isChecked ->  }
    }


    fun addSubs(){
        binding!!.pbTranslatingWords.visibility=View.VISIBLE
        binding!!.errorTranslationMessage.visibility=View.GONE
        binding
        vm.is_translator_available.observe(viewLifecycleOwner, Observer{
            state->
            if(state is ModelDownloadState.Loading){
                binding!!.pbTranslatingWords.visibility=View.VISIBLE
                binding!!.errorTranslationMessage.visibility=View.GONE
            }else if(state is ModelDownloadState.Successs){

                binding!!.pbTranslatingWords.visibility=View.GONE
                binding!!.errorTranslationMessage.visibility=View.GONE

            }
            else{
                binding!!.pbTranslatingWords.visibility=View.GONE
                binding!!.errorTranslationMessage.visibility=View.VISIBLE
            }

        })

        vm.translate_status.observe(viewLifecycleOwner, Observer { state ->
            if (state is TranslateMultipleWordsStatus.Loading) {
                binding!!.pbTranslatingWords.visibility = View.VISIBLE
                binding!!.errorTranslationMessage.visibility = View.GONE
                binding!!.buttonRetranslate.visibility=View.GONE
            } else if (state is TranslateMultipleWordsStatus.Success) {
                Log.e("FRAGMENT TRANSLATE","translated values are ${vm.translated_words.values.toList()}")
                addTextstoChip(
                    vm.translated_words.values.toMutableList(),
                    binding!!.cgTranslatedWords
                )
                binding!!.titleAttribution.visibility=View.VISIBLE
                binding!!.iconGoogleTranslate.visibility=View.VISIBLE
                binding!!.pbTranslatingWords.visibility = View.GONE
                binding!!.errorTranslationMessage.visibility = View.GONE
                binding!!.buttonRetranslate.visibility=View.VISIBLE
                binding!!.buttonSaveToMyDictionary.visibility=View.VISIBLE

            } else if (state is TranslateMultipleWordsStatus.Error) {
                binding!!.pbTranslatingWords.visibility = View.GONE
                binding!!.errorTranslationMessage.visibility = View.VISIBLE
                binding!!.errorTranslationMessage.text = state.error_message

            } else {

            }

        }
        )
    }

                        //SETTING UP LISTENERS
    fun setUpListeners(){

        binding!!.deleteTranslatedTexts.setOnClickListener {

            if(binding!!.cgTranslatedWords.size>0){
                binding!!.cgTranslatedWords.removeAllViews()
            }
            if(vm.translated_words.size>0){
                vm.resetTranslatedWords()
            }
            binding!!.titleAttribution.visibility=View.GONE
            binding!!.iconGoogleTranslate.visibility=View.GONE
            binding!!.buttonRetranslate.visibility=View.VISIBLE
            binding!!.buttonSaveToMyDictionary.visibility=View.GONE

        }

         binding!!.buttonRetranslate.setOnClickListener {
             if(binding!!.cgTranslatedWords.size>0 && it.isVisible){
                 binding!!.cgTranslatedWords.removeAllViews()

             }
             vm.translateSelectedWords()
             binding!!.buttonSaveToMyDictionary.isEnabled=true

         }




                // RESETS TRANSLATED WORDS IN VIEWMODEL MAKES SURE EVERY DATA STRUCTURED USED IN RAM IS FREE WHENEVER POSSIBLE

//            binding!!.resetSelectedWordsChips.setOnClickListener {
//                binding!!.cgSelectedWords.clearCheck()
//                vm.resetTranslatedWords()
//                setUpSelectedChipsListener()
//            }


                            // SETUP TO HIGHLIGHT TRANSLATED WORD WHENEVER WORD FROM SELECTED WORD IS SELECTED
                            // MAPS WORDS TO MEANING CHIPS

                            setUpSelectedChipsListener()

                            binding!!.buttonSaveToMyDictionary.setOnClickListener {
//                                setUpSnackBarMessage("starting!!!")
//                                snackbar!!.show()
                                vm.save_words_to_dictionary()
                                vm.saving_words_status.observe(viewLifecycleOwner,Observer{
                                    state->
                                    when(state){
                                        is UserProcessState.Loading->{
                                            Toast.makeText(this.context,"loading...",Toast.LENGTH_SHORT).show()
                                            binding!!.buttonSaveToMyDictionary.isEnabled=false

                                        }
                                        is UserProcessState.Success->{
                                           Toast.makeText(this.context,"success xD",Toast.LENGTH_SHORT).show()

//                                            this.findNavController().navigate(FragTranslateDirections.actionFragTranslateToFragHome(state.size))
                                            this.findNavController().popBackStack(R.id.frag_home,false,false)

                                        }
                                        is UserProcessState.Error->{
                                            binding!!.buttonSaveToMyDictionary.isEnabled=true
                                            Toast.makeText(this.context,"failed :(",Toast.LENGTH_SHORT).show()


                                        }
                                        else->{
                                            binding!!.buttonSaveToMyDictionary.isEnabled=true
                                        snackbar!!.setText("oops! somethign went wrong")
                                    }
                                    }
                                })
                            }




    }
                                // WHEN USER TAPS ON SELECTED WORD CHIP TRANSLATED CHIP GETS HIGHLIGHTED
    fun setUpSelectedChipsListener(){
        binding!!.cgSelectedWords.setOnCheckedStateChangeListener { group, checkedIds ->
            if(binding!!.cgTranslatedWords.size>0){
                if (checkedIds.isNotEmpty()) {
                    for (id in checkedIds){
                        val selected_chip=group.findViewById(id) as Chip
                        val translated_chip= binding!!.cgTranslatedWords.findViewWithTag<Chip>(vm.translated_words.get(selected_chip.text))
                        val default_chip_color=selected_chip.chipBackgroundColor

                        selected_chip.setOnCheckedChangeListener { buttonView, isChecked ->
                            if(isChecked){
                                selected_chip.chipBackgroundColor=
                                    ColorStateList.valueOf(android.graphics.Color.GREEN)
                                if(translated_chip!=null){
                                    translated_chip.chipBackgroundColor=ColorStateList.valueOf(android.graphics.Color.GREEN)
                                    translated_chip.isChecked=true
                                }

                            }else{

                                selected_chip.chipBackgroundColor=default_chip_color
                                if(translated_chip!=null){
                                    translated_chip.chipBackgroundColor=default_chip_color
                                    translated_chip.isChecked=false
                                }

                            }
                        }

                    }
                }
            }else{
                return@setOnCheckedStateChangeListener
            }
        }

    }


                        //CHECK WHETHER A CHIP WITH SPECIFIED ID EXIST IN CHIPGROUP

    fun doChipExist(id:Int,group: ChipGroup):Boolean{

        val view=group.findViewById<Chip>(id)
        if(view==null){
            return false
        }else{
            return true
        }
    }



                //REMOVE CHIP FROM CHIPGROUP

    fun removeChip(id:Int,group: ChipGroup){

        val view=group.findViewById<Chip>(id) as Chip
        group.removeView(view)

    }


                    //RESETS CHIP GROUP TO DEFAULT UNCHECK STATE

    fun resetChipGroup(group:ChipGroup){
        group.clearCheck()
    }


    fun setUpSnackBarMessage(message:String){
        snackbar=Snackbar.make(binding!!.root,message,Snackbar.LENGTH_INDEFINITE)
    }



}