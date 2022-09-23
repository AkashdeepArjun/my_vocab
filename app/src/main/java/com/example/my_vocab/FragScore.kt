package com.example.my_vocab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.example.my_vocab.databinding.FragScoreBinding

class FragScore: Fragment() {

    private var binding:FragScoreBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragScoreBinding.inflate(inflater)
        return binding!!.root
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun nullifyBindings(){

        binding=null
    }

}