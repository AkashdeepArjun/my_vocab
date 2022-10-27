package com.example.my_vocab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_vocab.DateConvertorHelper
import com.example.my_vocab.UserProcessState
import com.example.my_vocab.data.datamodel.Vocab
import com.example.my_vocab.repo.BaseRepo
import com.example.my_vocab.repo.VocabRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class MyDictionaryViewModel @Inject constructor(val repo: BaseRepo):ViewModel(){

    private val _all_words:MutableLiveData<List<Vocab>> = MutableLiveData<List<Vocab>>()
    var all_words:LiveData<List<Vocab>> = _all_words



        init {

            loadMyWords()
        }




    fun loadMyWords()=viewModelScope.launch{

        all_words=repo.getAllVocabs()

    }


//    fun filter(query:String?)=viewModelScope.launch{
////        if(query!=null){
////            filtered_dictionary.addAll(fetched_dictionary.filter { it.word.contains("q") })
////        }
////        else{
////            filtered_dictionary.clear()
////        }
//
//    }




}