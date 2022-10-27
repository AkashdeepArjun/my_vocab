package com.example.my_vocab

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun <T> LiveData<T>.getOrAwaitValue(
    time:Long=2,
    timeunit:TimeUnit=TimeUnit.SECONDS,
    after_observe:()->Unit={}


):T{

    var data:T? =null
    val latch=CountDownLatch(1)
    val observer=object :Observer<T>{
        override fun onChanged(t: T) {
            data=t
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    try{
        after_observe.invoke()
        if(!latch.await(time,timeunit)){
            throw  TimeoutException("live data was never set")
        }

    }finally {
        this.removeObserver(observer)
    }


    @Suppress("UNCHECKED_CAST")
    return data as T

}