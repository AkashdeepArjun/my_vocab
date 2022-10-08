package com.example.my_vocab.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.my_vocab.DateConvertorHelper
import com.example.my_vocab.data.datamodel.Score
import com.example.my_vocab.data.datamodel.Vocab
import com.example.my_vocab.repo.VocabDao
import timber.log.Timber

@Database(entities = [Vocab::class, Score::class], version = 2, exportSchema = false)
@TypeConverters(DateConvertorHelper::class)
abstract class VocabDatabase:RoomDatabase() {

    abstract fun vocabDao():VocabDao
    companion object{


        @Volatile
        private var INSTANCE:VocabDatabase?=null

        fun getDatabaseInstance(context: Context):VocabDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(context.applicationContext,
                VocabDatabase::class.java,"my_vocab_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE=instance
                Timber.tag("ROOM DATABASE CREATION").v("INSTANCE CREATED WITH HASHCODE ${INSTANCE.hashCode()}")
                instance
            }

        }


    }


}