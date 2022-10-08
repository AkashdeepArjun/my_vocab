package com.example.my_vocab.data.datamodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "scores")
data class Score(

    @PrimaryKey(autoGenerate = true)val id:Int=0,
    @ColumnInfo(name="score")val score:Float=0.0f,
    @ColumnInfo(name="quiz_type")val quiz_type:String="",
    @ColumnInfo(name="date")val date: Date?
)
