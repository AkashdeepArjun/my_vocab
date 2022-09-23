package com.example.my_vocab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.example.my_vocab.databinding.FragQuizBinding

class Frag_Quiz: Fragment() {


    private var binding:FragQuizBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragQuizBinding.inflate(inflater)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun nullifyBindings(){
        binding==null
    }

}
