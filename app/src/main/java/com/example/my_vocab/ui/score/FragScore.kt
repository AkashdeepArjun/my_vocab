package com.example.my_vocab.ui.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.my_vocab.R
import com.example.my_vocab.WorkProgressState
import com.example.my_vocab.adapters.ScoresAdapter
import com.example.my_vocab.databinding.FragScoreBinding
import com.example.my_vocab.ui.quiz.FragInQuizGameArgs
import com.example.my_vocab.viewmodels.MyViewModelFactory
import com.example.my_vocab.viewmodels.ScoresViewModel
import com.example.my_vocab.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FragScore: Fragment() {

    @Inject
    lateinit var scoresAdapter:ScoresAdapter

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory

    private lateinit var vm:ScoresViewModel

    private var binding:FragScoreBinding?=null


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
        setUpViewModel()
        initThings()
        subToData()

    }

    override fun onResume() {
        super.onResume()
    }

        fun setUpViewModel(){
            vm=ViewModelProvider(this,viewModelFactory).get(ScoresViewModel::class.java)
        }
    private fun initThings(){
        binding!!.recentScores.adapter=scoresAdapter
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun nullifyBindings(){

        binding=null
    }

    fun subToData(){

        vm.fetch_scores_status.observe(viewLifecycleOwner, Observer {
          state->
            when(state){
                is WorkProgressState.SUCCESS->{
                    binding!!.retreiveStatus.visibility=View.GONE
                    scoresAdapter.differ.submitList(vm.scores)
                    binding!!.recentScores.visibility=View.VISIBLE

                }
                is WorkProgressState.STARTED->{
                    binding!!.retreiveStatus.text=state.message

                }
                is WorkProgressState.RUNNNIN->{
                    binding!!.retreiveStatus.text=state.message
                }
                is WorkProgressState.FAILED->{
                    binding!!.retreiveStatus.text=state.message
                }
                else->{}
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}