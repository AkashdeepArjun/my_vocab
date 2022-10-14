package com.example.my_vocab.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.my_vocab.R
import com.example.my_vocab.UserProcessState
import com.example.my_vocab.adapters.MyDictionaryAdapter
import com.example.my_vocab.databinding.ActivityMyDictionaryBinding
import com.example.my_vocab.viewmodels.MyDictionaryViewModel
import com.example.my_vocab.viewmodels.MyViewModelFactory
import com.example.my_vocab.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyDictionary : AppCompatActivity() {

    private var binding: ActivityMyDictionaryBinding? = null
    lateinit var myDictionaryAdapter: MyDictionaryAdapter

    @Inject
    lateinit var vmf: MyViewModelFactory



    private lateinit var viemodel: MyDictionaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDictionaryBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.title = "My dictionary"
        setUpViewModel()
        setupAdapter()
        subscibe()
        setupSearchView()
    }



    private fun setUpViewModel() {

        viemodel = ViewModelProvider(this, vmf)[MyDictionaryViewModel::class.java]

    }

    private fun setupAdapter() {
        myDictionaryAdapter=MyDictionaryAdapter(viemodel)
        binding!!.rvMyWords.adapter = myDictionaryAdapter
    }

    private fun subscibe() {

        viemodel.state_loading_words.observe(this, Observer { state ->
            when (state) {
                is UserProcessState.Success -> {
                    binding!!.fetchingStatus.setTextColor(Color.GREEN)
                    binding!!.fetchingStatus.text = state.size.toString() + " successfully fetched"
                    binding!!.animationView.cancelAnimation()
                    binding!!.animationView.visibility = View.GONE
                    binding!!.fetchingStatus.visibility = View.GONE
                    binding!!.rvMyWords.visibility = View.VISIBLE
                    myDictionaryAdapter.differ.submitList(viemodel.fetched_dictionary)
                }
                is UserProcessState.Loading -> {
                    binding!!.fetchingStatus.setTextColor(Color.MAGENTA)
                    binding!!.fetchingStatus.text = state.message
                }
                is UserProcessState.Error -> {

                    binding!!.fetchingStatus.setTextColor(Color.RED)
                    binding!!.fetchingStatus.text = state.error_message

                }
            }

        })
    }


    fun setupSearchView() {

        binding!!.svMyWords.queryHint=""
        val listener = object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                myDictionaryAdapter.filter.filter(query)
                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {

                myDictionaryAdapter.filter.filter(newText)
                return true
            }

        }
        binding!!.svMyWords.setOnQueryTextListener(listener)
//        binding!!.svMyWords.setQuery("search words here",false)

    }
}