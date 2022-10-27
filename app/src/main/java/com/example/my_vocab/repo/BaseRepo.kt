package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab

interface BaseRepo {


     fun getAllVocabs(): LiveData<List<Vocab>>

     fun insert(vocab: List<Vocab>)

     fun deleteAllVocabs()

     fun delete(vocab: Vocab)

     fun saveScore(score: Score)

     fun getAllScores(): LiveData<List<Score>>

     fun deleteAllScores()




}