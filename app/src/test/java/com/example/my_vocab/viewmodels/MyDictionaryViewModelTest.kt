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
class MyDictionaryViewModelTest {

    @get:Rule
    val instantExecuterRule= InstantTaskExecutorRule()

    lateinit var my_dictionary_view_model:MyDictionaryViewModel
    lateinit var repo:FakeRepo


    @Before
    fun setup(){

        repo= FakeRepo()
        my_dictionary_view_model=MyDictionaryViewModel(repo)

    }



    @Test
    fun getAllWords(){

        val data=my_dictionary_view_model.repo.getAllVocabs().getOrAwaitValue()
        assertThat(data.size,`is`(2))


    }





}