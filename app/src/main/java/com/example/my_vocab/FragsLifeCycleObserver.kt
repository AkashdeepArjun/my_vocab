package com.example.my_vocab

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.sourceInformation
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.my_vocab.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


class FragsLifeCycleObserver(private val viewModel: SharedViewModel):LifecycleEventObserver {


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {


        when(event){

            Lifecycle.Event.ON_DESTROY->{
                Timber.tag("MY FRAG OBSERVER").e("Destroy function called!")
                viewModel.resetEverything()
                viewModel.resetSelectedWords()
                viewModel.resetTranslatedWords()

            }

            Lifecycle.Event.ON_START->{
//

                       }

            Lifecycle.Event.ON_STOP->{

            }

        }


    }
}