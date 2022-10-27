package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab

@Dao
interface VocabDao :BaseDao{
@Query("SELECT * FROM my_dictionay ORDER BY word ASC")
    override fun getAllVocabs(): LiveData<List<Vocab>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override   fun insert(vocab: List<Vocab>)


    @Query("DELETE FROM my_dictionay")
    override  fun deleteAll()


    @Delete
    override fun delete(vocab: Vocab)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override   fun saveScore(score: Score)


    @Query("SELECT * FROM scores ORDER BY date DESC")
    override  fun getAllScores(): LiveData<List<Score>>

    @Query("DELETE from scores")
    override   fun deleteAllScores()


}