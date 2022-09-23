package com.example.my_vocab

import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


// FILE LOGGING CLASS  CREDITS :https://medium.com/u/681cfa648238?source=post_page-----493a8ec7a10c--------------------------------
class DebugLogger: Timber.DebugTree() {


                //  HUMAN READABLE FORMAT OF LOGS AND LOG FILE
                    lateinit var log_date:Date

                    //LOG OUTPUT FILE



//                    var  LOG_TIME_STAMP_FORMAT:String
//                    var FILE_TIME_STAMP_FORMAT:String

                    companion object{
                        var deleted_log_file_succees=false
                        val VOCAB_APP_LOG_TAG=DebugLogger::class.java.simpleName
                        val log_date:Date=Date()
                       val  LOG_TIME_STAMP_FORMAT=SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                        val FILE_TIME_STAMP_FORMAT=SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Date())

                                    //LOG FILE FOR APPLICATION
                        var log_file:File? = null


                            //HELPER FUNCTION TO CREATE  FILE JUST PROVIDE  PATH AND FILE_NAME
            fun generateFile(path:String,file_name:String){
//                var file:File?=null

                    if(haveExternalStorageSupport()){
                        var log_root_directory:File=File(Environment.getExternalStorageDirectory().absolutePath,BuildConfig.APPLICATION_ID+File.separator+path)
                        var log_directory_exists:Boolean=true
                        if(!log_root_directory.exists()){
                            log_directory_exists=log_root_directory.mkdirs()
                        }

                        if(log_directory_exists){
                            log_file=File(log_root_directory,file_name)
                        }


                    }

//                return file

            }

                                //
        private fun haveExternalStorageSupport():Boolean{
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
        }

                        //CLEAR LOGS FILE

        fun clearLogs(){
            if(log_file!=null && log_file!!.exists()){
                log_file!!.delete()
                deleted_log_file_succees=true
            }else{
                deleted_log_file_succees=false
            }
        }
    }





    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val type:String =when(priority){
            6->"ERROR"
            3->"DEBUG"
            5->"WARNING"
            4->"INFO"
            2->"VERBOSE"
            7->"ASSERT"

            else->{"unknown"}
        }
        try {

            val path:String="Log"
            val file_name=FILE_TIME_STAMP_FORMAT+".txt"
            generateFile(path,file_name)
            if(log_file!=null){

                var file_writer:FileWriter=FileWriter(log_file,true)
                file_writer.append("                                 ")
                file_writer.append("\n")
                file_writer.append("~ ~ ~ ~ ~ ~ ~ ${LOG_TIME_STAMP_FORMAT} ~ ~ ~ ~ ~ ~ ~ ~ ~ ~")
                file_writer.append("\n")
                file_writer.append("                                                           ")
                file_writer.append("\n")
                file_writer.append("         TYPE :${type}\n         CLASS/TAG:${tag}\n         MESSAGE: ${message}\n")
//                    .append(message)
//                    .append("\n")
                    file_writer.flush()
                    file_writer.close()
            }



        }catch (exception:Exception){
            Log.e(VOCAB_APP_LOG_TAG,"error while logging into file message "+exception)
        }



    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element)+" - "+element.lineNumber
    }




}

