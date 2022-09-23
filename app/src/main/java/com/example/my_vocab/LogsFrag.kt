package com.example.my_vocab

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.my_vocab.databinding.LogsFragBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileReader
import kotlin.math.log


class LogsFrag :Fragment(),MenuProvider{


    private var menuHost:MenuHost?=null
    private var status: Boolean=false
    var lines:List<String> ? =null
    var reader: FileReader?=null
    lateinit var path:String
    lateinit var file_name:String
    lateinit var log_directory: File
    var log_file:File? = null
    lateinit var binding:LogsFragBinding
//        File(Environment.getExternalStorageDirectory().absolutePath,BuildConfig.APPLICATION_ID+ File.separator+path+ File.separator+file_name)


    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path="Log"
        file_name=DebugLogger.FILE_TIME_STAMP_FORMAT+".txt"
        log_directory =
        File(Environment.getExternalStorageDirectory().absolutePath,BuildConfig.APPLICATION_ID+ File.separator+path)
        log_file=File(log_directory,file_name)

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=LogsFragBinding.inflate(requireActivity().layoutInflater)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuHost=requireActivity()
        menuHost!!.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.STARTED)
        lifecycleScope.launchWhenStarted {
            status=readLogs()
        }.invokeOnCompletion {

            if(status){
                Snackbar.make(binding!!.root,"success ",Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(binding!!.root,"no logs found :( ",Snackbar.LENGTH_SHORT).show()

            }

        }



    }



            //READ LOGS FROM FILE

    private  fun readLogs():Boolean{
        if(!log_file!!.exists()){
            return false
        }
        var log_read_success:Boolean=false
        try {

            reader = FileReader(log_file)
             lines =reader!!.readLines()
            for(line in lines!!){

                binding.logsOutput.append(line+"\n")

            }

            log_read_success=true
        }catch (exception:Exception){
            Timber.e("error while reading log file messagee ${exception.message}")
            log_read_success=false
            exception.printStackTrace()

        }
//        finally {
//            reader!!.close()
//        }
        return log_read_success
    }


//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val menu_item_clear_logs:MenuItem=menu.findItem(R.id.menu_item_clear_logs)
//        val menu_item_logs:MenuItem=menu.findItem(R.id.menu_item_logs)
//        menu_item_logs.isVisible=false
//        menu_item_logs.setEnabled(false)
//        menu_item_clear_logs.isVisible=false
//        menu_item_clear_logs.setEnabled(false)
//    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.log_frag_options_menu,menu)
        menu.iterator().forEach {
            if(it.itemId==R.id.menu_item_clear_logs){
                it.isVisible=true
            }else{
                it.isVisible=false
            }
        }


    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
                R.id.menu_item_clear_logs->{
                    DebugLogger.clearLogs()
                    if(DebugLogger.deleted_log_file_succees){
                        findNavController().popBackStack(R.id.frag_home,false)
                    }
                    true
                }

            else->false
        }
    }
}