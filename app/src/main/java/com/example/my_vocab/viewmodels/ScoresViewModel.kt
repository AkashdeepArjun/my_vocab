package com.example.my_vocab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_vocab.WorkProgressState
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.repo.BaseRepo
import com.example.my_vocab.repo.VocabRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScoresViewModel @Inject constructor(val repo:BaseRepo):ViewModel() {

        private var _all_scores:LiveData<List<Score>> =MutableLiveData<List<Score>>()
            var all_scores:LiveData<List<Score>> = _all_scores

    val scores= mutableListOf<Score>()


    init {
        getAllScores()
    }

    
    fun getAllScores()=viewModelScope.launch{

        all_scores=repo.getAllScores()

    }

    override fun onCleared() {
        super.onCleared()
        scores.clear()
    }


}