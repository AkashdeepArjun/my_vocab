package com.example.my_vocab.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab
import java.util.*

class FakeRepo:BaseRepo
{

    private val vocabs= mutableListOf<Vocab>(Vocab(1,"what","क्या"),Vocab(2,"determined","निर्धारित"))


    private val scores = mutableListOf<Score>(
        Score(1,0.5f,"rapid Test", Date(System.currentTimeMillis())),
        Score(2,0.5f,"Progress Test",Date(System.currentTimeMillis())),
        Score(3,0.5f,"Progress Test",Date(System.currentTimeMillis()))
    )



    override fun getAllVocabs(): LiveData<List<Vocab>> {

        return MutableLiveData(vocabs)

    }

    override fun insert(vocab: List<Vocab>) {

        vocab.forEach {
            if(!vocabs.contains(it)){
                vocabs.add(it)
            }
        }

    }

    override fun deleteAllVocabs() {

        vocabs.clear()

    }


    override fun delete(vocab: Vocab) {


        vocabs.remove(vocab)

    }


    override fun saveScore(score: Score) {

        scores.add(score)
    }


    override fun getAllScores(): LiveData<List<Score>> {

        return MutableLiveData(scores)
    }

    override fun deleteAllScores() {
            scores.clear()
    }

}
