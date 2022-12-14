package com.example.my_vocab

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.my_vocab.databinding.FragHomeBinding
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class Frag_Home:Fragment() {
    var snackbar:Snackbar?=null
    private var binding:FragHomeBinding? = null
    private val vm: SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=FragHomeBinding.inflate(requireActivity().layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding!!.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        vm.getAllVocabs()
        subToLiveData()
        Toast.makeText(context,"frag home  view created",Toast.LENGTH_SHORT).show()
        Timber.tag("FRAG HOME").v("fragment home created with viewmodel hashcode ${vm.hashCode()}")
//        showSnackBar("")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

//       vm.getAllVocabs()
    }


            //ON START METHOD OF FRAGMENT

    override fun onStart() {
        super.onStart()
//        vm.getAllVocabs()
        Toast.makeText(context,"frag home started",Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(context,"frag home stopped",Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(context,"frag home destroyed",Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(context,"frag home resume",Toast.LENGTH_SHORT).show()

    }

                //SETUP LISTENERS
                fun setUpListeners(){
                    binding!!.buttonLearn.setOnClickListener {
                        view->


                        this.findNavController().navigate(Frag_HomeDirections.actionFragHomeToFragCapture())

                    }
                }


                    //SUBSCRIBE TO CHANGES TO DATA
    fun subToLiveData(){

        vm.fethed_vocab_state.observe(viewLifecycleOwner, Observer {
            state->
            when(state){
                is UserProcessState.Loading->{

                    binding!!.numberOfWords.text="loading..."

                }
                is UserProcessState.Success->{
                    binding!!.numberOfWords.text=state.size.toString()
//                    snackbar!!.show()

                }
                is UserProcessState.Error->{
                    binding!!.numberOfWords.text="0"
                }
                else->{

                }
            }
        })

    }

    fun showSnackBar(message:String){
        snackbar=Snackbar.make(binding!!.root,message,Snackbar.LENGTH_SHORT)

    }






}