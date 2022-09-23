package com.example.my_vocab.viewmodels

import android.app.Application
import android.view.View
import androidx.camera.core.impl.CameraRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.my_vocab.repo.VocabRepo
import timber.log.Timber
import javax.inject.Inject
import kotlin.jvm.Throws

class MyViewModelFactory @Inject constructor( val application: Application,val repo: VocabRepo):

    ViewModelProvider.Factory {

    init {
        Timber.tag("VIEWMODEL FACTORY").v(" factory instance created with hashcode ${this.hashCode()}")
        Timber.tag("VIEWMODEL FACTORY").v("repo instance injected with hashcode ${repo.hashCode()}")
        Timber.tag("VIEWMODEL FACTORY").v("application instance created with hashcode ${application.hashCode()}")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SharedViewModel::class.java)){
            SharedViewModel(application,repo) as T
        }else{
           throw IllegalArgumentException("no valid viewmodel found")
        }
    }
}