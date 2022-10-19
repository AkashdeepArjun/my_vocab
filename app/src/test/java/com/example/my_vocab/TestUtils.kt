package com.example.my_vocab

import android.icu.text.DecimalFormat
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.log10


import kotlin.math.pow

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestUtils {



            //TESTING 3 DIGIT NUBERS TO STRING
            @Test
            fun test3digit(){
                assertEquals("521",DateConvertorHelper.MyUtils.NumToString(521))
            }



            //TESTING 4 DIGIT NUMBERS TO STRING
    @Test
    fun testNumToStringFloatingPointLessThan4(){

        assertEquals("1.2K",DateConvertorHelper.MyUtils.NumToString(1242))
    }

    @Test
    fun testNumToStringFloatingPointGreaterThan4(){

        assertEquals("1.2K",DateConvertorHelper.MyUtils.NumToString(1252))

    }


    //TESTING 5 DIGIT NUMBER
    @Test
    fun test5digit(){
        assertEquals("12.5K",DateConvertorHelper.MyUtils.NumToString(12521))
    }

    //  TESTING 6 DIGITS TO STRING CONVERSION
    @Test
    fun test6Digit(){
        assertEquals("125.6K",DateConvertorHelper.MyUtils.NumToString(125621))
    }

            //TESTTING 7 DIGITS
    @Test
    fun test7Digit(){
        assertEquals("1.2M",DateConvertorHelper.MyUtils.NumToString(1256217))
    }

    //TESTING 8 DIGITS

    @Test
    fun test8Digit(){
        assertEquals("12.5M",DateConvertorHelper.MyUtils.NumToString(12562178))
    }

    //TESTING 9 DIGITS
    @Test
    fun test9Digit(){
        assertEquals("125.6M",DateConvertorHelper.MyUtils.NumToString(125621789))
    }

    //TESTING 10 DIGITS
    @Test
    fun test10Digit(){
        assertEquals("2.1B",DateConvertorHelper.MyUtils.NumToString(Int.MAX_VALUE))
    }





    //TESTS POWER OF TEN  FOR 4 DIGIT NUMBER

    @Test
    fun testPowerOfTen4digits(){

        assertEquals(3.0,findPowerOfTen(1162),0.0)

    }
        // TESTS POWER OF TEN FOR A SINGLE DIGIT

    @Test
    fun testPowerOfTenSingleDigit(){

        assertEquals(0.0,findPowerOfTen(1),0.0)

    }


    // FIND THE POWER OF 10

    fun findPowerOfTen(num: Int): Double {

        val power_of_ten: Double = floor(log10(num.toDouble()))
        return power_of_ten

    }


    // RETURNS THE FLOATING POINT VALUE




}