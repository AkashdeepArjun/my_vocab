package com.example.my_vocab.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_vocab.ModelDownloadState
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppStarterViewModel @Inject constructor(val translator: Translator): ViewModel() {


    private var conditions: DownloadConditions?=null
    var eng_to_hindi_translator: Translator?=null


    private val _is_translator_available: MutableLiveData<ModelDownloadState> = MutableLiveData<ModelDownloadState>(ModelDownloadState.Loading())
    val is_translator_available: LiveData<ModelDownloadState> =_is_translator_available

    init {
        download_translator_model()
    }


    fun download_translator_model()=viewModelScope.launch(Dispatchers.IO){

        _is_translator_available.postValue(ModelDownloadState.Loading("downloading translator please be patient"))
        conditions=DownloadConditions
            .Builder()
            .requireWifi()
            .build()

            translator
            .downloadModelIfNeeded(conditions!!)
            .addOnSuccessListener {
                _is_translator_available.postValue(ModelDownloadState.Successs("translator download success!!"))
            }
            .addOnFailureListener {
                _is_translator_available.postValue(ModelDownloadState.Error(it))

            }


    }

}