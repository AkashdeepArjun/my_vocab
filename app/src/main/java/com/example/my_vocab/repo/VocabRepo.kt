package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.my_vocab.data.datamodel.Vocab
import timber.log.Timber

class VocabRepo(val dao: BaseDao) {

    init {

        Timber.tag("VOCAB REPO").v("repo instance created with ${this.hashCode()}")

    }

    suspend fun getAllVocabs():List<Vocab> = dao.getAllVocabs()


     suspend fun insert(vocabs: List<Vocab>) = dao.insert(vocabs)


    suspend fun deleteAll() = dao.deleteAll()

     suspend fun delete(vocab: Vocab) = dao.delete(vocab)


}