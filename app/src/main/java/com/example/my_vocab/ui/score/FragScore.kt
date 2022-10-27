package com.example.my_vocab.ui.score

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.iterator
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
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FragScore: Fragment(),MenuProvider {

    private  var menuHost: MenuHost?=null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menu.iterator().forEach {
            it.isEnabled=false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        return false
    }

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
        enterTransition=inflater.inflateTransition(R.transition.slide_from_right)
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
        setUpOptionMenu()
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

        vm.all_scores.observe(viewLifecycleOwner, Observer {
            list->
            if(list.isNotEmpty()){

                scoresAdapter.differ.submitList(list)
                binding!!.recentScores.visibility=View.VISIBLE
                binding!!.scoreLoadingAnim.cancelAnimation()
                binding!!.scoreLoadingAnim.visibility=View.GONE


            }
   })

    }

    fun setUpOptionMenu(){

        menuHost=requireActivity()
        menuHost!!.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.STARTED)

    }


    override fun onDestroy() {
        super.onDestroy()


    }
}