package com.example.my_vocab

import android.os.Message
import android.view.Display
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.Exception

sealed class UserProcessState{
    data class Success(val size:Int):UserProcessState()
    data class Error(val error_message: String?):UserProcessState()
    data class Loading(val message: String?):UserProcessState()

}

sealed class TextDetectionState{
    data class Successs(val list:MutableList<String>):TextDetectionState()
    data class Error(val exception: Exception):TextDetectionState()
    data class Loading(val message:String):TextDetectionState()
}

sealed class ModelDownloadState{
    data class Successs(val message: String?=null):ModelDownloadState()
    data class Error(val exception: Exception):ModelDownloadState()
    data class Loading(val message:String?=null):ModelDownloadState()

}


sealed class TranslationState{

    data class Success(val translated_text:String):TranslationState()
    data class Error(val error_message:String?=null):TranslationState()
    data class Loading(val message:String?=null):TranslationState()


}

sealed class TranslateMultipleWordsStatus{
    data class Success(val did_it_work: Boolean?=null):TranslateMultipleWordsStatus()
    data class Error(val error_message:String?=null):TranslateMultipleWordsStatus()
    data class Loading(val message:String?=null):TranslateMultipleWordsStatus()

}


fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner,observer:(T)->Unit){

    observe(owner,object :Observer<T>{
        override fun onChanged(t: T) {
           removeObserver(this)
            observer(t)
        }
        })



}