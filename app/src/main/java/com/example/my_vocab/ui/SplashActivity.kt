package com.example.my_vocab.ui

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.my_vocab.ModelDownloadState
import com.example.my_vocab.R
import com.example.my_vocab.databinding.ActivitySplashBinding
import com.example.my_vocab.viewmodels.AppStarterViewModel
import com.example.my_vocab.viewmodels.MyViewModelFactory
import com.example.my_vocab.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var vmf: MyViewModelFactory

    private lateinit var viemodel:AppStarterViewModel


    companion object{

        val REQUIRED_PERMISSIONS= mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        ).toTypedArray()



    }
    private var binding:ActivitySplashBinding?=null
    private var activity_result_launcher: ActivityResultLauncher<Array<String>>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setUpActivityResultLauncher()
        if(!checkAllPermissionsGranted()){
            activity_result_launcher!!.launch(REQUIRED_PERMISSIONS)
        }
        setUpViewModel()        //SETS UP VIEW MODEL
        setupObservers()
        setContentView(binding!!.root)
    }

    fun setUpActivityResultLauncher()
    {

        activity_result_launcher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions->
            var l=0
            permissions.entries.forEach {
                if(it.value){
                    l++
                }
                if(it.key==WRITE_EXTERNAL_STORAGE && it.value){

                }
            }
            if(l==permissions.entries.size){

                        // STARTS DOWNLOADING TRANSLATOR ONCE  GOT ALL THE PERMISSIONS GRANTED

                           //UPDATES USER THE PROGRESS OF DOWNLOADING VIEWMODEL

//                binding!!.permissionsStatus.setImageDrawable(resources.getDrawable(R.drawable.done))
//                binding!!.statusText.text="permisions granted"

            }else{

//                getAllPermissiions()

//                binding!!.modelDownloadStatus.text="please grant strage"

            }

        }


    }


    fun setupObservers()
    {
        viemodel.is_translator_available.observe(this){
                state->
            when(state){
                is ModelDownloadState.Loading->{
                    binding!!.modelDownloadStatus.text=state.message
                }
                is ModelDownloadState.Successs->{

                    binding!!.modelDownloadStatus.text=state.message
                    val intent= Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    this.finish()

                }
                else->{
                    binding!!.modelDownloadStatus.text="error !! while downloading model"

                }
            }

        }

    }





    private fun getAllPermissiions(){

        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
            MainActivity.REQUEST_CODE_PERMISSION
        )
    }



    fun checkAllPermissionsGranted():Boolean{
        val all_permissions_granted = REQUIRED_PERMISSIONS.all { permission->
            ContextCompat.checkSelfPermission(baseContext,permission)== PackageManager.PERMISSION_GRANTED

        }
        return all_permissions_granted
    }


    private fun setUpViewModel(){
        viemodel= ViewModelProvider(this,vmf).get(AppStarterViewModel::class.java)

    }



}