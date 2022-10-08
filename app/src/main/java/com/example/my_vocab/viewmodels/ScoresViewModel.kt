package com.example.my_vocab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_vocab.WorkProgressState
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.repo.VocabRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoresViewModel @Inject constructor(val repo:VocabRepo):ViewModel() {

    private val _fetch_scores_status: MutableLiveData<WorkProgressState> =
        MutableLiveData<WorkProgressState>(WorkProgressState.STARTED(null))
    val fetch_scores_status: LiveData<WorkProgressState> = _fetch_scores_status

    val scores= mutableListOf<Score>()


    init {
        getAllScores()
    }

    fun getAllScores()=viewModelScope.launch{

        _fetch_scores_status.postValue(WorkProgressState.STARTED("started"))

        kotlin.runCatching {
            _fetch_scores_status.postValue(WorkProgressState.RUNNNIN("fetching scores!!!"))

            repo.getAllScores()

        }.onSuccess {
            scores.addAll(it)
            _fetch_scores_status.postValue(WorkProgressState.SUCCESS("score fetch success"))
        }.onFailure {

            _fetch_scores_status.postValue(WorkProgressState.FAILED("fetching failed"))
        }

    }

    override fun onCleared() {
        super.onCleared()
        scores.clear()
    }


}