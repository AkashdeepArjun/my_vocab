package com.example.my_vocab.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.my_vocab.DebugLogger
import com.example.my_vocab.databinding.FragCaptureBinding
import com.example.my_vocab.ui.MainActivity
import com.example.my_vocab.ui.SplashActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
@AndroidEntryPoint
class Frag_capture:Fragment() {

    private var activity_result_launcher: ActivityResultLauncher<Array<String>>?=null
    private lateinit var logger: DebugLogger
    private var required_permissions_granted=false



    companion object{

        private const val FRAGMENT_TAG="CAPTURE PHOTO FRAGMENT"
        private const val FILE_FORMAT="yyyy-MM-dd-HH-mm-ss-SSS"
        val REQUIRED_PERMISSIONS= mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).toTypedArray()
    }

    private var snackbar: Snackbar?=null
    private var binding:FragCaptureBinding? = null

    private var image_capture:ImageCapture? = null

    private lateinit var camera_executor:ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflator=TransitionInflater.from(requireContext())
        enterTransition=inflator.inflateTransition(com.example.my_vocab.R.transition.slide_from_right)
        exitTransition=inflator.inflateTransition(com.example.my_vocab.R.transition.slide_from_left)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragCaptureBinding.inflate(requireActivity().layoutInflater)
        logger= DebugLogger()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActivityResultLauncher()
        init_Camera()
        setupListeners()
        setUpSnackBar()

    }



            // starts the camera
    private fun init_Camera(){

        if(!checkAllPermissionsGranted()){
            binding!!.grantPermissionButton.visibility=View.VISIBLE
            binding!!.tvPermissionInfo.visibility=View.VISIBLE
        }else{
            start_camera()
        }

//            start_camera()
    }


    private fun setupListeners(){

        binding!!.grantPermissionButton.setOnClickListener {


            activity_result_launcher!!.launch(REQUIRED_PERMISSIONS)
        }

        binding!!.capturePhotoButton.setOnClickListener {
            view->

            capture_photo()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun nullifyEverything(){
        binding =null
        camera_executor.shutdown()
    }


                        //STARTS CAMERA

    @SuppressLint("UnsafeOptInUsageError")
    fun start_camera(){

        val camera_provider_future=ProcessCameraProvider.getInstance(this.requireActivity())

        camera_provider_future.addListener({

        val camera_provider:ProcessCameraProvider=camera_provider_future.get()

            // preview

            val preview=Preview.Builder().build().also {
                it.setSurfaceProvider(binding!!.cameraPreview.surfaceProvider)

            }

            image_capture = ImageCapture.Builder()
                .setTargetResolution(android.util.Size(binding!!.cameraPreview.width,binding!!.cameraPreview.height))
//                .setTargetResolution(android.util.Size(680,400))
                .build()


                    //selection of camera based on choice

            val camera_selector=CameraSelector.DEFAULT_BACK_CAMERA
            try {

                    camera_provider.unbindAll()

                    camera_provider.bindToLifecycle(this,camera_selector,preview,image_capture)



            }catch (
              exc:Exception
            ){
                Log.e(FRAGMENT_TAG,"use case binding failed",exc)
            }


        },ContextCompat.getMainExecutor(this.requireActivity()))


    }


                    //CAPTURES PHOTO

    fun capture_photo(){


            binding!!.capturePhotoButton.isEnabled=false

            //image_capture
                    val ic=image_capture?:return

                //file reference specified in yyyy-mm-dd format

            val name=SimpleDateFormat(FILE_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())

                    //creating meta data for new image created

        val content_values=ContentValues().apply {

            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if(Build.VERSION.SDK_INT> Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CameraX-image")
            }

        }

                //creating output options for a file

        val outputOptions=ImageCapture
            .OutputFileOptions.Builder(requireActivity().contentResolver,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,content_values)
            .build()
        snackbar!!.show()

        ic.takePicture(outputOptions,ContextCompat.getMainExecutor(this.requireActivity().baseContext),object:ImageCapture.OnImageSavedCallback{
             override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//
                 binding!!.capturePhotoButton.isEnabled=true
                 snackbar!!.setText("proces complete!").setDuration(Snackbar.LENGTH_LONG)
                 snackbar!!.dismiss()
                 val uri:Uri?=outputFileResults.savedUri
//                val bundle = bundleOf("captured_photo" to uri.toString())
                this@Frag_capture.findNavController().navigate(Frag_captureDirections.actionFragCaptureToFragDetectedTexts(
                    uri.toString()
                ))


            }



//            override fun onCaptureSuccess(image: ImageProxy) {
//                super.onCaptureSuccess(image)
////                val photo_link:Uri?=image.
//            }

            override fun onError(exception: ImageCaptureException) {
                binding!!.capturePhotoButton.isEnabled=false

                Log.e(FRAGMENT_TAG,"${exception.message}")
                Log.wtf(FRAGMENT_TAG,"lmfao what is even going on here ")
                Log.e(FRAGMENT_TAG,"lmfao meawwfao dafako and meaw")
                logger.log(Log.ERROR,"capture photo failed")
            }


        })
    }
    fun setUpSnackBar(){
        snackbar=Snackbar.make(binding!!.root,"processing!!!",Snackbar.LENGTH_SHORT)

    }

    fun checkZeroShutterSupport(){

//        if(CameraInfo.isZslSupported()){
//
//        }


    }


    fun setUpActivityResultLauncher()
    {

        activity_result_launcher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions->

           required_permissions_granted= permissions.entries.all {
               it.value
            }
            if(required_permissions_granted){
                binding!!.grantPermissionButton.visibility=View.GONE
                binding!!.tvPermissionInfo.visibility=View.GONE
                start_camera()
            }
            else{
                binding!!.grantPermissionButton.visibility=View.VISIBLE
                binding!!.tvPermissionInfo.visibility=View.VISIBLE
            }


        }


    }
    fun checkAllPermissionsGranted():Boolean{
        val all_permissions_granted = SplashActivity.REQUIRED_PERMISSIONS.all { permission->
            ContextCompat.checkSelfPermission(context!!,permission)== PackageManager.PERMISSION_GRANTED

        }
        return all_permissions_granted
    }


}