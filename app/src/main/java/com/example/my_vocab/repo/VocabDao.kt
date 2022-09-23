package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.my_vocab.data.datamodel.Vocab

@Dao
interface VocabDao :BaseDao{



    @Query("SELECT * FROM my_dictionay ORDER BY word ASC")
    override suspend fun getAllVocabs():List<Vocab>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(vocabs: List<Vocab>)


    @Query("DELETE FROM my_dictionay")
    override suspend fun deleteAll()


    @Delete
    override suspend fun delete(vocab: Vocab)

}