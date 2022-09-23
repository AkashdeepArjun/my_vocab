package com.example.my_vocab.data.datamodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_dictionay")
data class Vocab(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    @ColumnInfo(name = "word") val word:String,
    @ColumnInfo(name = "meaning") val meaning:String

)