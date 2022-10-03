package com.example.my_vocab.ui.quiz

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.res.colorResource
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.my_vocab.R
import com.example.my_vocab.databinding.FragQuizBinding
import com.example.my_vocab.observeOnce
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Frag_Quiz: Fragment() {

    var checked_option_id=0;
    private var binding:FragQuizBinding? = null
    private val vm:SharedViewModel by activityViewModels()

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
        init_instructions()
        setUpListeners()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun nullifyBindings(){
        binding=null
    }
    fun setUpListeners(){
            binding!!.groupQuizTestOptions.addOnButtonCheckedListener { group, checkedId, isChecked ->

                if(isChecked){
                    binding!!.buttonStartTest.isVisible=true
                    binding!!.groupQuizTestOptions.clearOnButtonCheckedListeners()
                }




            }

        binding!!.buttonStartTest.setOnClickListener {

            checked_option_id=when(binding!!.groupQuizTestOptions.checkedButtonId){
                R.id.button_progressor_test->2
                R.id.button_scholar_test->3
                else->1
            }

            this.findNavController().navigate(Frag_QuizDirections.goToGame(checked_option_id))


        }

    }

    fun init_instructions(){
        val instructions:Array<String> =resources.getStringArray(R.array.instructions)
        for(ins in instructions){
            binding!!.tvInstructions.append(ins)
        }
    }




}

