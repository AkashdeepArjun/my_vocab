package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab

interface BaseDao {

     fun getAllVocabs(): LiveData<List<Vocab>>

     fun insert(vocab: List<Vocab>)

       fun deleteAll()

       fun delete(vocab: Vocab)

       fun saveScore(score: Score)

       fun getAllScores():LiveData<List<Score>>

       fun deleteAllScores()

}