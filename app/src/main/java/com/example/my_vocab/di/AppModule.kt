package com.example.my_vocab.di

import android.app.Application
import android.content.Context
import com.example.my_vocab.adapters.MyDictionaryAdapter
import com.example.my_vocab.adapters.ScoresAdapter
import com.example.my_vocab.repo.BaseDao
import com.example.my_vocab.repo.BaseRepo
import com.example.my_vocab.repo.VocabDao
import com.example.my_vocab.repo.VocabRepo
import com.example.my_vocab.room_database.VocabDatabase
import com.example.my_vocab.viewmodels.MyViewModelFactory
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
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
        fun provideRepo(dao: BaseDao):BaseRepo=VocabRepo(dao)


                                //PROVIDES SINGLETON VIEWMODEL FACTORY
        @Singleton
        @Provides
        fun provideVmf(application: Application,repo: BaseRepo, remote_download_manager:RemoteModelManager,translator: Translator)=MyViewModelFactory(application,repo,remote_download_manager,translator)

                        //PROVIDES SCORES ADAPTER
        @Singleton
        @Provides
        fun provideAdapter()=ScoresAdapter()


                        //TRANSLATOR OPTIONS

        @Singleton
        @Provides
        fun provideTranslatorOptions():TranslatorOptions =TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build()


                //PROVIDES TRANSLATOR

        @Singleton
        @Provides
        fun provideTranslator(options:TranslatorOptions):Translator=Translation.getClient(options)


                // PROVIDES VOCAB ADAPTER

//        @Singleton
//        @Provides
//        fun provideVocabAdapter():MyDictionaryAdapter=MyDictionaryAdapter()


        @Singleton
        @Provides
        fun provideModelManager()=RemoteModelManager.getInstance()

        @Singleton
        @Provides
        fun provideTextRecognizer():TextRecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

}