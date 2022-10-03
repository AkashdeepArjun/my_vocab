package com.example.my_vocab.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.allViews
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.my_vocab.DebugLogger
import com.example.my_vocab.MyVocabApp
import com.example.my_vocab.TextDetectionState
import com.example.my_vocab.databinding.FragDetectTextsBinding
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber

@AndroidEntryPoint
class FragDetectTexts: Fragment() {

    //viewmodel

    private val vm: SharedViewModel by activityViewModels()

    private var job: Job? = null
    lateinit var detected_texts: MutableList<String>
    private var input_image: InputImage? = null
    private var text_recognizer: TextRecognizer? = null
    private var photo_uri: Uri? = null
    private val args: FragDetectTextsArgs by navArgs()

    private var binding: FragDetectTextsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init_textRecognizer()
        detected_texts = mutableListOf<String>()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {

            binding = FragDetectTextsBinding.inflate(inflater)

        } catch (e: Exception) {
            val message = e.message
            DebugLogger().log(Log.ERROR, message)
        }
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCapturedPhoto()
        vm.setUpPhotoUri(this.photo_uri!!)
        vm.prepareInputImage()
        setUpListeners()
        Timber.tag("FRAGMENT DETECT TEXTS").v("fragment detect texts with viewmodel hashcode ${vm.hashCode()}")
    }

    fun removeAllChips(group: ChipGroup){
        group.removeAllViews()
    }


    fun setUpListeners() {

        binding!!.buttonDetectTexts.setOnClickListener {
            binding!!.buttonDetectTexts.isEnabled = false
            binding!!.textDetectionPb.visibility = View.VISIBLE
            binding!!.buttonTranslate.visibility=View.GONE

            try {


                if(binding!!.cgDetectedTexts.size!=0){
                    removeAllChips(binding!!.cgDetectedTexts)

                }

                vm.detect_texts(photo_uri!!)

                vm.textReadState.observe(viewLifecycleOwner, Observer {  state->
                    if (state is TextDetectionState.Loading) {
                        binding!!.buttonDetectTexts.isEnabled = false
                        binding!!.textDetectionPb.visibility = View.VISIBLE

                    } else if (state is TextDetectionState.Successs) {
                        binding!!.buttonDetectTexts.isEnabled = true
                        binding!!.textDetectionPb.visibility = View.GONE
//                            vm.setUpPhotoUri(null)



                        if(state.list.size!=0){
                            addTextstoChip(state.list)

                        }
                        if(vm.finished_detecting_texts){
                            vm.resetEverything()
//                            vm.textReadState.removeObservers(this)
                        }

//                        vm.resetEverything()

//
//                            vm.resetEverything()

//                        vm.resetEverything()
                    } else {
                        binding!!.buttonDetectTexts.isEnabled = true
                        binding!!.textDetectionPb.visibility = View.GONE
                        Toast.makeText(
                            context, "dunno wtf happened", Toast.LENGTH_SHORT
                        ).show()
                    }
                })


            } catch (exception: Exception) {
                Timber.e("DETECTION TEXTS BUG WITH MESSAGE ${exception.message}")
                MyVocabApp.my_logger.log(
                    Log.ERROR,
                    "RECOGNIZE TEXTS ISSUE WITH message ${exception.message}"
                )
            }


        }

            if(binding!!.cgDetectedTexts.size>0){
                binding!!.cgDetectedTexts.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.isNotEmpty()) {
                        binding!!.buttonTranslate.visibility = View.VISIBLE
                        for (id in checkedIds){
                            val chip=group.findViewById(id) as Chip
                            val text=chip.text.toString()

                            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                                if(isChecked){
                                    if(!vm.selected_words.contains(text)){
                                        vm.selected_words.add(text)
                                    }
                                }else{
                                    if(vm.selected_words.contains(text)){
                                        vm.selected_words.remove(text)
                                    }
                                }
                            }

                        }
                    } else {
                        vm.resetSelectedWords()
                        binding!!.buttonTranslate.visibility = View.GONE

                    }
                }
            }else{
                binding!!.cgDetectedTexts.setOnCheckedStateChangeListener(null)
            }


                            //selecting items_listener


                            //translate selected words listener

            binding!!.buttonTranslate.setOnClickListener {

                this.findNavController().navigate(FragDetectTextsDirections.actionFragDetectedTextsToFragTranslate())
            }



                        // DELETES DETECTED TEXT CHIPS TRIGGERED BY PRESSING DELETE BUTTON GIVES USER TO RESCAN IMAGE AGAIN

            binding!!.buttonDeleteDetectedTexts.setOnClickListener {
                removeAllChips(binding!!.cgDetectedTexts)
                binding!!.buttonTranslate.visibility=View.GONE
            }

    }



        fun removeObservers(){
            vm.textReadState.removeObservers(this)
        }

    //adding detected texts in chips

    fun addTextstoChip(list: MutableList<String>) {
        for (text in list) {
            val chip = Chip(this.context)
            chip.text = text
            chip.isCloseIconVisible = false
            chip.isCheckable = true
            binding!!.cgDetectedTexts.addView(chip as View)

        }
    }

    //setup captured photo

    fun setupCapturedPhoto() {

        photo_uri = Uri.parse(args.imageUri)
//        binding!!.capturedPhoto.setImageURI(photo_uri)

        Glide.with(this).load(photo_uri).into(binding!!.capturedPhoto)
    }

    // setting up the text recognizer thing
    fun init_textRecognizer() {
        text_recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

        fun setChipListenersToChipGroup(group: ChipGroup){

            val chips =group.allViews


        }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun resetEveryThing(){
//        binding=null
        input_image=null
        text_recognizer=null
        photo_uri=null
        vm.setUpPhotoUri(null)
//        binding!!.cgDetectedTexts.removeAllViews()
        job=null
        vm.resetEverything()

//        job!!.cancel("fragment is finished",Throwable(cause = null, message = "fragment is killed"))
//        removeObservers()
    }

            //add detected chips to group


}