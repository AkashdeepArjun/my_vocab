package com.example.my_vocab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.get
import androidx.navigation.ui.*
import com.example.my_vocab.databinding.ActivityMainBinding
import com.example.my_vocab.viewmodels.MyViewModelFactory
import com.example.my_vocab.viewmodels.SharedViewModel
import com.google.android.gms.common.api.internal.LifecycleFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileReader
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var vmf:MyViewModelFactory

    private lateinit var viemodel:SharedViewModel
    var logger: DebugLogger?=null
    companion object{
        var all_permissions_granted:Boolean=false
        private  const val TAG="MY VOCAB APP"
        private const val FILE_FORMAT="yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSION=364
        val REQUIRED_PERMISSIONS= mutableListOf(Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).toTypedArray()
    }

    private var app_bar_configuration: AppBarConfiguration?=null
    private lateinit var nav_controller: NavController
    private lateinit var nav_host_fragment: NavHostFragment
    private  var binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if(checkAllPermissionsGranted())
        {
           showToast(this,"all required permissions granted")
            setUpViewModel()
            setUpBottomNavigation()
        }else{
           showToast(this,"please grant storage and internet permissions",true)
            getAllPermissiions()

        }


        Log.e("ACTIVITY","CREATED!!!")
//        Timber.tag("MAIN ACTIVITY").v(" viewmodel instantiated with hashcode ${viemodel.hashCode()}")
//        Timber.tag("MAIN ACTIVITY").v("viewmodel factory instantiated viewmodel with hashcode ${vmf.hashCode()}")

    }

                //setting up bottom navigations


    fun setUpBottomNavigation(){
        nav_host_fragment=supportFragmentManager.findFragmentById(R.id.frag_container) as NavHostFragment
        nav_controller=nav_host_fragment.navController
        setUpActionBarConfiguration(nav_controller)
                  // manual handling of fragments aaa
        setupBottomNavigationItemListener()
//        binding!!.bottomNavView.setupWithNavController(nav_controller)



    }


                                 //  LISTENS TO DESTINATION CHANGE
                fun setupBottomNavigationItemListener(){

                    binding!!.bottomNavView.setOnItemSelectedListener {
                        item->
                        when(item.itemId){
                            R.id.frag_home->{
                                nav_controller.popBackStack(nav_controller.graph.startDestinationId,false)
                                binding!!.bottomNavView.findViewById<BottomNavigationItemView>(item.itemId).isActivated=true
                                true}
                            R.id.frag_quiz->{
                                nav_controller.popBackStack(R.id.frag_home,false)
                                nav_controller.navigate(item.itemId)
//                                binding!!.bottomNavView.invalidate()
                                binding!!.bottomNavView.findViewById<BottomNavigationItemView>(item.itemId).isActivated=true

                                true
                            }
                            R.id.frag_score->{
                                nav_controller.popBackStack(R.id.frag_home,false)
                                nav_controller.navigate(item.itemId)
//                                binding!!.bottomNavView.invalidate()

                                binding!!.bottomNavView.findViewById<BottomNavigationItemView>(item.itemId).isActivated=true






                                true
                            }
                            else->{
//                                item.isChecked=false
                                false}
                        }
                    }


                                     //WHEN ITEM RESELECTED
                         binding!!.bottomNavView.setOnItemReselectedListener {

                                 item->
                                if(item.itemId!=R.id.frag_home){
                                    nav_controller.popBackStack(R.id.frag_home,false)
                                    nav_controller.navigate(item.itemId)
                                }else{
                                    nav_controller.popBackStack(nav_controller.graph.startDestinationId,false)
                                }
//                             binding!!.bottomNavView.invalidate()
                             binding!!.bottomNavView.findViewById<BottomNavigationItemView>(item.itemId).isActivated=true

                             true
                             }



        }

                                      //ACTION BAR NAVIGATION SETUP

            fun setUpActionBarConfiguration(nav_controller:NavController){
            app_bar_configuration= AppBarConfiguration(setOf(R.id.frag_home,R.id.frag_quiz,R.id.frag_score))
//            app_bar_configuration= AppBarConfiguration(nav_controller.graph)
            setupActionBarWithNavController(nav_controller,app_bar_configuration!!)

        }


            // GOAL:SHOWS BACK BUTTON ON ACTION BAR


    override fun onSupportNavigateUp(): Boolean {
        return nav_controller.navigateUp(app_bar_configuration!!)
    }




                //CHECKS IF REQUIRED PERMISSIONS ARE GRANTED

    fun checkAllPermissionsGranted():Boolean{
        all_permissions_granted= REQUIRED_PERMISSIONS.all { permission->
            ContextCompat.checkSelfPermission(baseContext,permission)== PackageManager.PERMISSION_GRANTED

        }
        return  all_permissions_granted
    }


                //SHOWS TOAST

    fun showToast(context: Context,message:String,should_be_long:Boolean=false){
        if(should_be_long){
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        }else{

            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()

        }


    }
            // INFLATES OPTIONS MENU

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu_main,menu)
//        menu!!.findItem(R.id.menu_item_clear_logs)
        return true

    }

        // LISTENS TO OPTION MENU ITEM CLICKS

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_item_logs->{
                nav_controller.navigate(Frag_HomeDirections.actionFragHomeToFragLogs())
                true}

                else->
                    super.onOptionsItemSelected(item)

        }
    }

                //REQUEST REQUIRED PERMISSIONS
    private fun getAllPermissiions(){

          ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
    }





    override fun onResume() {
        super.onResume()
        showToast(this,"activity resumed")

    }


    private fun setUpViewModel(){
        viemodel=ViewModelProvider(this,vmf).get(SharedViewModel::class.java)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var l=0;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== REQUEST_CODE_PERMISSION ){
            grantResults.forEach {
                result->
                if(result==RESULT_OK){
                    l++;
                }else{
                    l--;
                }

            }
            if(l==permissions.size){
                showToast(this,"permissions granted!!")
            }
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }




}