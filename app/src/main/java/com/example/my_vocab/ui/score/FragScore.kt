package com.example.my_vocab.ui.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.my_vocab.R
import com.example.my_vocab.databinding.FragScoreBinding
import com.example.my_vocab.ui.quiz.FragInQuizGameArgs
import com.example.my_vocab.viewmodels.SharedViewModel

class FragScore: Fragment() {

    private var binding:FragScoreBinding?=null
    private val vm:SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater= TransitionInflater.from(requireContext())
        exitTransition=inflater.inflateTransition(R.transition.slide_from_left)
        enterTransition=inflater.inflateTransition(R.transition.slide_from_left)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragScoreBinding.inflate(inflater)
//        binding!!.tvScore.text=args.scoreDest.toString()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScore()
    }


    fun setScore(){
        binding!!.tvScore.text=vm.score.toString()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun nullifyBindings(){

        binding=null
    }

}