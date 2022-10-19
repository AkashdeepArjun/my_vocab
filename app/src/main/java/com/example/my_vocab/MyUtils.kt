package com.example.my_vocab

import android.os.Message
import android.view.Display
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.TypeConverter
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

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

sealed class TimerState{
    data class DONE(val message: String?=null):TimerState()
    data class Error(val error_message: String?=null):TimerState()
    data class RUNNING(val message: String?):TimerState()
    data class STARTTED(val message: String?):TimerState()
    data class INITIALIZE(val message: String?):TimerState()

}

sealed class WorkProgressState{

    data class STARTED(val message: String?):WorkProgressState()
    data class RUNNNIN(val message: String?):WorkProgressState()
    data class SUCCESS(val message: String?):WorkProgressState()
    data class FAILED(val message: String):WorkProgressState()


}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner,observer:(T)->Unit){

    observe(owner,object :Observer<T>{
        override fun onChanged(t: T) {
           removeObserver(this)
            observer(t)
        }
        })



}

class DateConvertorHelper{
    @TypeConverter
    fun fromTimeStamp(value:Long?): java.util.Date?{
        return value?.let { java.util.Date(it)}
    }

    @TypeConverter
    fun dateToTimeStamp(date:java.util.Date?):Long?{
        return date?.time?.toLong()
    }


    object  MyUtils{

        fun NumToString(num:Int):String{
            val format=DecimalFormat("##.#")
            val power_of_ten: Double = floor(log10(num.toDouble()))
            val number_of_digits=power_of_ten+1
            if(power_of_ten<=2.0)
            {
                return num.toString()
            }
            var result:String=""

            when(power_of_ten){
                in 3.0 ..5.0->{

                    format.roundingMode=RoundingMode.DOWN
                    val d:Double= (num/ (10.0.pow(3.0))).absoluteValue
//                val res= String.format(Locale.getDefault(),"%.1f",d).toDouble()
                    val res=format.format(d)
                    result=res.toString()+"K"
                }
                in 6.0 ..8.0->{

                    format.roundingMode=RoundingMode.DOWN
                    val d:Double= (num/ (10.0.pow(6.0))).absoluteValue
//                val res= String.format(Locale.getDefault(),"%.1f",d).toDouble()
                    val res=format.format(d)
                    result=res.toString()+"M"

                }
                in 9.0..Double.MAX_VALUE ->{
                    format.roundingMode=RoundingMode.DOWN
                    val d:Double= (num/ (10.0.pow(9.0))).absoluteValue
//                val res= String.format(Locale.getDefault(),"%.1f",d).toDouble()
                    val res=format.format(d)
                    result=res.toString()+"B"

                }
                else->{}
            }


            return result
        }
    }



}


