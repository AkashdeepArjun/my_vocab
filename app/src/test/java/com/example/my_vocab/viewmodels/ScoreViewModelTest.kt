package com.example.my_vocab.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.my_vocab.getOrAwaitValue
import com.example.my_vocab.repo.FakeRepo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ScoreViewModelTest {



    @get:Rule
    val instantExecuterRule= InstantTaskExecutorRule()

    private lateinit var repo: FakeRepo
    private lateinit var score_view_model:ScoresViewModel

    @Before
    fun setup(){
        repo= FakeRepo()
        score_view_model= ScoresViewModel(repo)
    }


    @Test
    fun getScoresFromViewModel(){
        val data=score_view_model.repo.getAllScores().getOrAwaitValue()
        assertThat(data.size,`is`(3))
    }



}