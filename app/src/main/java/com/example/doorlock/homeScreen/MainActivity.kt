package com.example.doorlock.homeScreen

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.doorlock.R
import com.example.doorlock.SecurityQuestionActivity
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivityMainBinding
import com.example.doorlock.doorThemeSelection.DoorThemesActivity
import com.example.doorlock.intruderDetect.IntruderDetection
import com.example.doorlock.lockTypeSelection.LockTypeActivity
import com.example.doorlock.passwordSelection.PatternPasswordActivity
import com.example.doorlock.passwordSelection.PinPasswordActivity
import com.example.doorlock.services.MyForegroundService
import com.example.doorlock.soundSelection.SoundSelectionActivity
import com.example.doorlock.wallpaperSelection.WallpaperThemesActivity


class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val CAMERA_PERMISSION_REQUEST = 1001
    private lateinit var navigation: NavigateActivity
    private var checkFlag : Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigation = NavigateActivity(this)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val answer = sharedPreferences.getString("secutirtyAnser", "")
                if (binding.lockCheckbox.isChecked) {
                    if (answer == "") {
                        Toast.makeText(this@MainActivity, "Set recovery question first", Toast.LENGTH_SHORT).show()
                    }else{
                        finish()
                    }
                }else{
                    finish()
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        checkFlag = sharedPreferences.getBoolean("Flagcheck",false)
        if(checkFlag){
            startingCard(true,"Successfully Set",R.color.green)
            startService()
            val notificationManager = getSystemService(NotificationManager::class.java)
            val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
            if (!areNotificationsEnabled) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
            else{
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST
                    )
                }
            }
        }else {
            startingCard(false,"Please first set up your door lock ",R.color.red)
            stopService()
        }
    }
    private fun startingCard(lockCheckBox:Boolean, text:String, colorTest: Int){
        binding.lockCheckbox.isChecked = lockCheckBox
        val color  = ContextCompat.getColor(this,colorTest)
        binding.LockSetup.setCardBackgroundColor(color)
        binding.runningtext.setTextColor(color)
        binding.runningtext.text = text
        startService()
    }
     fun onClickMethod(view: View) {
        when (view.id) {
            R.id.wallpapers -> {
                navigation.navigateTo(WallpaperThemesActivity::class.java)
                finish()
            }
            R.id.intruders->{
                navigation.navigateTo(IntruderDetection::class.java)
            finish()
            }
            R.id.door_themes -> {
                navigation.navigateTo(DoorThemesActivity::class.java)
                finish()
            }
            R.id.LockSetup ->{
                checkFlag = sharedPreferences.getBoolean("Flagcheck",false)
                if(!checkFlag){
                    navigation.navigateTo(LockTypeActivity::class.java)
                    finish()

                }else{
                    val pinpassword =sharedPreferences.getInt("pinpassword",-1)
                    val patternpassword =sharedPreferences.getInt("patternpass",-1)

                    if(pinpassword == 1) {
                        navigation.navigateTo(PinPasswordActivity::class.java)
                        finish()
                    }else if(patternpassword == 1){
                        navigation.navigateTo(PatternPasswordActivity::class.java)
                        finish()
                    }else{
                        Toast.makeText(this@MainActivity,"INVALID",Toast.LENGTH_SHORT).show()
                    }

                }

            }

            R.id.sounds -> {
                navigation.navigateTo(SoundSelectionActivity::class.java)
                finish()
            }

            R.id.recovery -> {
                navigation.navigateTo(SecurityQuestionActivity::class.java)
                finish()
            }

        }
        }
    private fun startService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        startService(serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)

    }
}
