package com.example.my_vocab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_vocab.DateConvertorHelper
import com.example.my_vocab.UserProcessState
import com.example.my_vocab.data.datamodel.Vocab
import com.example.my_vocab.repo.VocabRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class MyDictionaryViewModel @Inject constructor(val repo: VocabRepo):ViewModel(){

    private val _state_loading_words:MutableLiveData<UserProcessState> = MutableLiveData(UserProcessState.Loading("loading...."))
    val state_loading_words:LiveData<UserProcessState>  = _state_loading_words

    val fetched_dictionary= mutableListOf<Vocab>()
    var filtered_dictionary= mutableListOf<Vocab>()
        init {

            loadMyWords()
        }




    fun loadMyWords()=viewModelScope.launch{
        _state_loading_words.postValue(UserProcessState.Loading("just few moment..."))
        kotlin.runCatching {

            repo.getAllVocabs()
        }.onSuccess {
            fetched_dictionary.addAll(it)
            _state_loading_words.postValue(UserProcessState.Success(DateConvertorHelper.MyUtils.NumToString(it.size)))

        }.onFailure {
            _state_loading_words.postValue(UserProcessState.Error("well that is weird!!! "))
        }

    }


    fun filter(query:String?)=viewModelScope.launch{
        if(query!=null){
            filtered_dictionary.addAll(fetched_dictionary.filter { it.word.contains("q") })
        }
        else{
            filtered_dictionary.clear()
        }

    }




}