package com.example.my_vocab.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
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
class SharedViewModelTest{

    private lateinit var svm:SharedViewModel
    private lateinit var repo: FakeRepo

    @Before
    fun setup(){
        repo= FakeRepo()
        svm= SharedViewModel(ApplicationProvider.getApplicationContext(),repo)

    }

    @get:Rule
    val instantExecuterRule=InstantTaskExecutorRule()



                //TESTS SHARED VIEWMODEL :FETCHING DATA
    @Test
    fun getVocabsFromViewModel(){
        svm=SharedViewModel(ApplicationProvider.getApplicationContext(),FakeRepo())
        val data=svm.repo.getAllVocabs().getOrAwaitValue()
        assertThat(data.size,`is`(2))

    }
                    //TEST SCOREVIEWMODEL :FETCHNG SCORES
    //@Test
    //fun getScoresFromScoreViewModel(){
      //  val data=score_view_model.repo.getAllScores().getOrAwaitValue()
    //                    assertThat(data.size,`is`(3))
    //}




}