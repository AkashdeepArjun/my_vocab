package com.example.my_vocab.di

import android.app.Application
import android.content.Context
import com.example.my_vocab.adapters.ScoresAdapter
import com.example.my_vocab.repo.BaseDao
import com.example.my_vocab.repo.VocabDao
import com.example.my_vocab.repo.VocabRepo
import com.example.my_vocab.room_database.VocabDatabase
import com.example.my_vocab.viewmodels.MyViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

                                //PROVIDES APPLICATION INSTNANCE AS A SINGLETON

//        @Singleton
//        @Provides
//        fun provideApplication():Application= Application()

                                //PROVIDES DATABASE SINGLE INSTANCE

        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context):VocabDatabase=VocabDatabase.getDatabaseInstance(context)

                                // PROVIDES DATA ACCESS OBJEECT  (DAO) AS SINGLETON

        @Singleton
        @Provides
        fun provideDao(db:VocabDatabase):BaseDao=db.vocabDao()


                                //PROVIDES THE REPO AS SINGLETON

        @Singleton
        @Provides
        fun provideRepo(dao: BaseDao)=VocabRepo(dao)


                                //PROVIDES SINGLETON VIEWMODEL FACTORY
        @Singleton
        @Provides
        fun provideVmf(application: Application,repo: VocabRepo)=MyViewModelFactory(application,repo)

        @Singleton
        @Provides
        fun provideAdapter()=ScoresAdapter()
}