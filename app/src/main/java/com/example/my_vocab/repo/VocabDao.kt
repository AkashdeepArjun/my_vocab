package com.example.my_vocab.repo

import androidx.room.*
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab

@Dao
interface VocabDao :BaseDao{



    @Query("SELECT * FROM my_dictionay ORDER BY word ASC")
    override suspend fun getAllVocabs():List<Vocab>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(vocab: List<Vocab>)


    @Query("DELETE FROM my_dictionay")
    override suspend fun deleteAll()


    @Delete
    override suspend fun delete(vocab: Vocab)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun saveScore(score: Score)


    @Query("SELECT * FROM scores ORDER BY date DESC")
    override suspend fun getAllScores(): List<Score>

    @Query("DELETE from scores")
    override suspend fun deleteAllScores()


}