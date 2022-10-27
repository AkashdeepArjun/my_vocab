package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class VocabRepo(val dao: BaseDao):BaseRepo {

    init {

        Timber.tag("VOCAB REPO").v("repo instance created with ${this.hashCode()}")

    }

    override  fun getAllVocabs(): LiveData<List<Vocab>> =dao.getAllVocabs()

    override  fun insert(vocab: List<Vocab>) = dao.insert(vocab)

    override  fun deleteAllVocabs() = dao.deleteAll()

    override  fun delete(vocab: Vocab) = dao.delete(vocab)

    override  fun saveScore(score: Score)=dao.saveScore(score)

    override  fun getAllScores(): LiveData<List<Score>> = dao.getAllScores()

    override fun deleteAllScores()=dao.deleteAllScores()
}