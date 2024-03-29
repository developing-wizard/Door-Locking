package com.example.doorlock.passwordSelection


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.doorlock.R
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivityPatternPasswordBinding
import com.example.doorlock.homeScreen.MainActivity
import com.example.doorlock.lockTypeSelection.LockTypeActivity
import com.example.doorlock.services.MyForegroundService
import com.google.common.util.concurrent.ListenableFuture
import java.io.File


class PatternPasswordActivity : AppCompatActivity() {
    private lateinit var patternLockView: PatternLockViewActivity
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navigation: NavigateActivity
    private var check : Boolean = false
    private var currentAttempts = 0
    private var remainingAttempts = 3
    private var dialogShown = false
    private var mediaPlayer: MediaPlayer? = null
    val patternimagebackground = intArrayOf(
        R.drawable.door1,
        R.drawable.door2,
        R.drawable.door3,
        R.drawable.door4,
        R.drawable.door5,
        R.drawable.door6,
    )
    val soundselected = intArrayOf(
        R.raw.voice1,
        R.raw.voice2,
        R.raw.voice3,
        R.raw.voice4,
        R.raw.voice5,
    )
    private val items = listOf("Mothers Name?", "Fathers Name?", "Place of birth?", "Favourite pet?")
    private lateinit var photoFile: File
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
    private val capturedImagePaths = ArrayList<String>()
    private val binding by lazy {
        ActivityPatternPasswordBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigation = NavigateActivity(this)
        val cameraProvider: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        cameraProvider.addListener({ camera = cameraProvider.get()
            .bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture)
        }, ContextCompat.getMainExecutor(this))
        photoFile = File(getExternalFilesDir(null), "photo.jpg")
        imageCapture = ImageCapture.Builder().setTargetRotation(windowManager.defaultDisplay.rotation).build()


        patternLockView = findViewById(R.id.lockscreen)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val activityLayoutBackground = binding.patternbackgroundImageView
        val checkposition = sharedPreferences.getInt("item_selected", -1)
        for(i in 0..5){
            if(i==checkposition)
            {
                activityLayoutBackground.setImageResource(patternimagebackground[i])
            }
        }
        val patternset = sharedPreferences.getBoolean("broadPattern",false)
        if(patternset){
            binding.patternbackbtn.visibility = View.GONE
            binding.forgotbtnpattern.visibility = View.VISIBLE
            startStickyActivity()
        }else{
            binding.forgotbtnpattern.visibility = View.GONE
            binding.patternbackbtn.visibility = View.VISIBLE
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val flag = sharedPreferences.getBoolean("Flagcheck", false)
                if(flag) {
                    navigation.navigateTo(MainActivity::class.java)
                    finish()
                }else{
                    navigation.navigateTo(LockTypeActivity::class.java)
                    finish()
                }
            }
        })
        binding.titlepattern.text = getString(R.string.draw_your_pattern)
        patternLockView.setOnPatternCompleteListener(object :
            PatternLockViewActivity.OnPatternCompleteListener {
            override fun onPatternComplete(pattern: List<Int>) {
                val flag = sharedPreferences.getBoolean("Flagcheck", false)
                if (!flag) {
                    if (!check) {
                        if(pattern.size < 3){
                            showToast("Minimum 3 dots")
                        }else{
                            val patternString = pattern.joinToString(",")
                            sharedPreferences.edit().putString("pattern_key", patternString).apply()
                            binding.titlepattern.text = getString(R.string.confirm_your_pattern)
                            showToast("Draw again to confirm pattern")
                            check = true
                            onBackPressedDispatcher
                        }
                    } else {
                        val getPatternString = sharedPreferences.getString("pattern_key", "") ?: ""
                        val patternList = getPatternString.split(",").map { it.toInt() }
                        if (pattern == patternList) {
                            showToast("Pattern Set")
                            startService()
                            binding.forgotbtnpattern.visibility = View.VISIBLE
                            sharedPreferences.edit().putBoolean("Flagcheck", true).apply()
                            sharedPreferences.edit().putInt("pinpassword", 0).apply()
                            sharedPreferences.edit().putInt("patternpass", 1).apply()
                            check = false
                            navigation.navigateTo(MainActivity::class.java)
                            finish()

                        } else {
                            if (currentAttempts < 2) {
                                currentAttempts += 1
                                remainingAttempts -= 1
                                showToast("Incorrect(attempts remaining$remainingAttempts)")
                            } else {
                                currentAttempts = 0
                                remainingAttempts = 3
                                binding.titlepattern.text = getString(R.string.enter_your_pattern)
                                showToast("Seems you forget your previous kindly reset it")
                                check = false
                            }
                        }
                    }
                } else if (flag) {
                    val pinget = sharedPreferences.getInt("patternpass", -1)
                    if (pinget == 1) {
                        val getPatternString = sharedPreferences.getString("pattern_key", "") ?: ""
                        val patternList = getPatternString.split(",").map { it.toInt() }
                        if (pattern == patternList) {
                            if (patternset) {
                                sharedPreferences.edit().putBoolean("broadPattern", false).apply()
                                removeStickyActivity()
                                showToast("Unlocked")
                                finish()
                            }else if(!patternset) {
                                sharedPreferences.edit().putBoolean("Flagcheck", false).apply()
                                sharedPreferences.edit().putInt("pinpassword", 0).apply()
                                sharedPreferences.edit().putInt("patternpass", 0).apply()
                                sharedPreferences.edit().putString("pattern_key", "").apply()
                                stopService()
                                onBackPressedDispatcher
                                navigation.navigateTo(MainActivity::class.java)

                                finish()
                            }
                        } else {
                            captureAndSavePhoto()
                            showToast("Pattern Doesnot Match")
                        }
                    }
                }
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.patternbackbtn -> {
                val flag = sharedPreferences.getBoolean("Flagcheck", false)
                if(flag) {
                    navigation.navigateTo(MainActivity::class.java)

                    finish()
                }else{
                    navigation.navigateTo(LockTypeActivity::class.java)
                    finish()
                }
            }
            R.id.forgotbtnpattern ->{
                if (!dialogShown) {
                    showCustomDialog()
                }
            }
        }
    }
    fun startService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        startService(serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)

    }
    private fun startStickyActivity() {
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.START or Gravity.TOP

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val parentView = binding.root.parent as? ViewGroup
        parentView?.removeView(binding.root)

        windowManager.addView(binding.root, layoutParams)
    }
    private fun showCustomDialog() {
        val question = sharedPreferences.getInt("question_selected",-1)
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        var textView = TextView(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.topMargin = 20
        textView.layoutParams = layoutParams
        if(question == -1) {
            textView.text = "Please set recovery question first :)"

        }else{
            textView.text = items[question]
        }
        textView.textSize = 18f
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        val editText = EditText(this)
        editText.gravity = Gravity.CENTER
        editText.setTextColor(Color.WHITE)
        container.addView(textView)
        container.addView(editText)
        val builder = AlertDialog.Builder(this)
        val titleView = TextView(this)
        titleView.text = "Security Question !"
        titleView.gravity = Gravity.CENTER
        titleView.textSize = 20f
        titleView.setTypeface(null, Typeface.BOLD)
        titleView.setTextColor(Color.WHITE)

        builder.setCustomTitle(titleView)
            .setView(container)
            .setPositiveButton("OK") { _, _ ->

                val answer = sharedPreferences.getString("secutirtyAnser", "")
                val enteredText = editText.text.toString()
                if(enteredText.isEmpty())
                {

                } else if (enteredText == answer) {
                    sharedPreferences.edit().putBoolean("broadPattern",false).apply()
                    sharedPreferences.edit().putBoolean("Flagcheck", false).apply()
                    sharedPreferences.edit().putInt("pinpassword", 0).apply()
                    sharedPreferences.edit().putInt("patternpass", 0).apply()
                    sharedPreferences.edit().putString("pinkey", "").apply()
                    removeStickyActivity()
                    stopService()
                    finish()
                    dialogShown = false
                } else {
                    showToast("Not Match")
                }

            }
            .setNegativeButton("Cancel") { _, _ ->
                dialogShown = false
            }

        val alertDialog = builder.create()
        alertDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialogShown = true
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.corner_radius_dialog)

        alertDialog.setOnDismissListener {
            dialogShown = false
        }
        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.WHITE)

            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(Color.WHITE)
        }
        alertDialog.show()
    }
    private fun removeStickyActivity() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if (binding.root.isAttachedToWindow) {
            val sound =  sharedPreferences.getInt("sound",-1)
           if(sound < 0){
               if (mediaPlayer == null || !mediaPlayer!!.isPlaying) {
                   mediaPlayer = MediaPlayer.create(this, soundselected[0])
               }
               mediaPlayer?.start()
           }else if (sound >= 0) {
                if (mediaPlayer == null || !mediaPlayer!!.isPlaying) {
                    mediaPlayer = MediaPlayer.create(this, soundselected[sound])
                }
                mediaPlayer?.start()
            }
            windowManager.removeView(binding.root)
        }
    }

    private fun captureAndSavePhoto() {
        val storageDir: File
        val timestamp = System.currentTimeMillis().toString()
        val photoFile: File
        val outputOptions: ImageCapture.OutputFileOptions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Intruder_Newer"
            )
            photoFile = File(storageDir, "photo_$timestamp.jpg")
            outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        } else {
            storageDir = File(Environment.getExternalStorageDirectory(), "Intruder_older")
            photoFile = File(storageDir, "photo_$timestamp.jpg")
            outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val photoPath = photoFile.absolutePath
                    capturedImagePaths.add(photoPath)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast("Cannot Capture")
                }
            }
        )
    }

}