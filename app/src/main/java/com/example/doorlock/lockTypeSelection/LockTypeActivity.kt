package com.example.doorlock.lockTypeSelection

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.example.doorlock.passwordSelection.PatternPasswordActivity
import com.example.doorlock.passwordSelection.PinPasswordActivity
import com.example.doorlock.R
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivityLockTypeBinding
import com.example.doorlock.homeScreen.MainActivity

class LockTypeActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLockTypeBinding.inflate(layoutInflater)
    }
    private lateinit var navigation : NavigateActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermission()
        navigation = NavigateActivity(this)
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })
    }


    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.backlock -> {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }

            R.id.pinlock ->
            {
                navigation.navigateTo(PinPasswordActivity::class.java)
                finish()
            }
            R.id.patternlock ->
            {
                navigation.navigateTo(PatternPasswordActivity::class.java)
                finish()
            }
        }
    }
    private fun requestPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + this.packageName)
            )
            startActivityForResult(intent, 1)
        }
    }
}