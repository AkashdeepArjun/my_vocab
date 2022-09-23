package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.my_vocab.data.datamodel.Vocab

interface BaseDao {



    suspend fun getAllVocabs():List<Vocab>

    suspend fun insert(vocab: List<Vocab>)

    suspend fun deleteAll()
    suspend fun delete(vocab: Vocab)



}