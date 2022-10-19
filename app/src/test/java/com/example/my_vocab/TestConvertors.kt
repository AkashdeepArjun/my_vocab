package com.example.my_vocab

import org.junit.Test
import android.icu.text.DecimalFormat
//import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.log10

class TestConvertors {

     var convertor:DateConvertorHelper?=null

    @Before
    fun setUpConvertor(){
         convertor=DateConvertorHelper()
    }

    @Test
    fun testLongToDate(){


       val date=convertor!!.fromTimeStamp(3000000L)

        assertEquals(3000000L,date!!.time)

    }


    @Test
    fun testDateToLong(){
        val date=Date(400000L)
        val milli =convertor!!.dateToTimeStamp(date)

        assertEquals(date.time,milli)
    }



}