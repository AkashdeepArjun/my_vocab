package com.example.my_vocab.ui.quiz

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.ui.res.colorResource
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.my_vocab.R
import com.example.my_vocab.databinding.FragQuizBinding
import com.example.my_vocab.observeOnce
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Frag_Quiz: Fragment() ,MenuProvider{

    private var menuHost: MenuHost?=null
    var checked_option_id=0;
    private var binding:FragQuizBinding? = null
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

        binding=FragQuizBinding.inflate(inflater)
        return binding!!.root
    }


    fun checkEligibility(){
        if(vm.fetched_vocabs.keys.size>=10){
            binding!!.miniumWordsCheck.visibility=View.GONE
            binding!!.quizRoot.visibility=View.VISIBLE
        }else{
            binding!!.miniumWordsCheck.visibility=View.VISIBLE
            binding!!.quizRoot.visibility=View.GONE
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOptionsMenu()
        checkEligibility()
        init_instructions()
        setUpListeners()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }


    fun disabeOptionsManu(){
        this.setMenuVisibility(false)
    }


    fun setUpOptionsMenu(){
        menuHost=requireActivity()
        menuHost!!.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.STARTED)

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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menu.iterator().forEach {
            it.isEnabled=false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        return false
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}

