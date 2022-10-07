package com.example.my_vocab.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.my_vocab.R
import com.example.my_vocab.databinding.FragMatchResultBinding
import com.example.my_vocab.viewmodels.SharedViewModel

class FragMatchResult: Fragment() {


    private var on_back_press_call_backs: OnBackPressedCallback?=null
    private var binding:FragMatchResultBinding?=null
    private val args:FragMatchResultArgs by navArgs()

    private val vm:SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater= TransitionInflater.from(requireContext())
        exitTransition=inflater.inflateTransition(R.transition.slide_from_left)
        enterTransition=inflater.inflateTransition(R.transition.slide_from_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragMatchResultBinding.inflate(inflater)
        return binding!!.root
    }

    fun initThings(){
        on_back_press_call_backs=object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.frag_quiz,false)

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,on_back_press_call_backs!!)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScore()
        setOnClickListeners()
        initThings()
    }

    fun setOnClickListeners(){
        binding!!.buttonRematch.setOnClickListener {

            findNavController().popBackStack(R.id.frag_in_quiz_game,false)
        }
        binding!!.buttonReselectQuiz.setOnClickListener {

            findNavController().popBackStack(R.id.frag_quiz,false)
        }

    }

    fun setScore(){
        var score=args.score
        binding!!.score.text=score.toString()

    }




}