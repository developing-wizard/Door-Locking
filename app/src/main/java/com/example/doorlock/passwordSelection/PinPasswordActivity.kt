package com.example.doorlock.passwordSelection

import android.app.AlertDialog
import android.app.NotificationManager
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
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
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
import com.example.doorlock.databinding.ActivityPinPasswordBinding
import com.example.doorlock.homeScreen.MainActivity
import com.example.doorlock.lockTypeSelection.LockTypeActivity
import com.example.doorlock.services.MyForegroundService
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import androidx.camera.core.ImageCapture.OutputFileOptions
import com.example.doorlock.activityChanger.NavigateActivity
import java.util.concurrent.locks.Lock


class PinPasswordActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPinPasswordBinding.inflate(layoutInflater)
    }
    private lateinit var navigation : NavigateActivity
    private lateinit var photoFile: File
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    private val capturedImagePaths = ArrayList<String>()
    private var dialogShown = false
    private var mediaPlayer: MediaPlayer? = null
    private var currentAttempts = 0
    private var pins : String = ""
    private  var remainingattempts = 3
    private lateinit var sharedPreferences: SharedPreferences
    private var checkpin : Boolean = false
    val pinimagebackground = intArrayOf(
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigation = NavigateActivity(this)

        val cameraProvider: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this@PinPasswordActivity)
        cameraProvider.addListener({ camera = cameraProvider.get()
            .bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture)
        }, ContextCompat.getMainExecutor(this))
        photoFile = File(getExternalFilesDir(null), "photo.jpg")
        imageCapture = ImageCapture.Builder().setTargetRotation(windowManager.defaultDisplay.rotation).build()

        val activityLayout = binding.pinbackgroundImageView
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val checkposition = sharedPreferences.getInt("item_selected", -1)
        for(i in 0..5){
            if(i == checkposition)
            {
                activityLayout.setBackgroundResource(pinimagebackground[i])
            }
        }

        val patternset = sharedPreferences.getBoolean("broadPattern",false)
        if(patternset){
            binding.pinbackbtn.visibility = View.GONE
            binding.forgotbtnnn.visibility = View.VISIBLE
            startStickyActivity()
        }else{
            binding.pinbackbtn.visibility = View.VISIBLE
            binding.forgotbtnnn.visibility = View.GONE
        }
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val flag = sharedPreferences.getBoolean("Flagcheck", false)
                if(flag) {
                    navigation.navigateTo(MainActivity::class.java)
                    finish()
                }else{
                    val animation = TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 1f,
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 0f
                    )
                    animation.duration = 1000
                    animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {
                        }
                        override fun onAnimationEnd(animation: Animation?) {
                            navigation.navigateTo(LockTypeActivity::class.java)
                            finish()
                        }
                        override fun onAnimationRepeat(animation: Animation?) {
                        }
                    })
                    val rootView = binding.root
                    rootView.startAnimation(animation)

                     }
            }
        })
        binding.passwordpin.showSoftInputOnFocus = false

    }

    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.pinbackbtn -> {
                val flag = sharedPreferences.getBoolean("Flagcheck", false)
                if(flag) {
                    navigation.navigateTo(MainActivity::class.java)
                    finish()
                }else{
                    navigation.navigateTo(LockTypeActivity::class.java)
                    finish()
                }
            }

            R.id.zero -> {
                binding.passwordpin.append("0")
            }

            R.id.one -> {
                binding.passwordpin.append("1")
            }

            R.id.two -> {
                binding.passwordpin.append("2")
            }

            R.id.three -> {
                binding.passwordpin.append("3")
            }

            R.id.four -> {
                binding.passwordpin.append("4")
            }

            R.id.five -> {
                binding.passwordpin.append("5")
            }

            R.id.six -> {
                binding.passwordpin.append("6")
            }

            R.id.seven -> {
                binding.passwordpin.append("7")
            }

            R.id.eight -> {
                binding.passwordpin.append("8")
            }

            R.id.nine -> {
                binding.passwordpin.append("9")
            }

            R.id.erase -> {
                val length: Int = binding.passwordpin.text.length
                if (length > 0) {
                    binding.passwordpin.text.delete(length - 1, length)
                }
            }
            R.id.forgotbtnnn -> {
                if (!dialogShown) {
                    showCustomDialog()
                }
            }
            R.id.pindonebtn -> {

                val flag = sharedPreferences.getBoolean("Flagcheck", false)
                if (!flag) {
                    if (binding.passwordpin.text.isEmpty()) {
                        showToast("Please enter pin")
                    }else if(binding.passwordpin.text.length < 4){
                        showToast("Minimum 4 digits")
                        binding.passwordpin.text.clear()
                    }
                    else {
                        val initialpin = binding.passwordpin.text.toString()
                        if (!checkpin) {
                            val pincheck = binding.passwordpin.text.toString()
                            pins = pincheck
                            sharedPreferences.edit().putString("pinkey", pins).apply()
                            binding.passwordpin.text.clear()
                            binding.pintitle.text = (getString(R.string.confirm_your_pin))
                            checkpin = true
                            onBackPressedDispatcher
                        } else {
                            val getpin = sharedPreferences.getString("pinkey", "")
                            if (getpin == initialpin) {
                                showToast("Password Set")
                                sharedPreferences.edit().putBoolean("Flagcheck", true).apply()
                                sharedPreferences.edit().putInt("pinpassword", 1).apply()
                                sharedPreferences.edit().putInt("patternpass", 0).apply()
                                binding.passwordpin.text.clear()
                                navigation.navigateTo(MainActivity::class.java)
                                finish()
                                startService()
                                checkpin = false
                            } else {
                                if (currentAttempts < 2) {
                                    currentAttempts += 1
                                    remainingattempts -= 1
                                    showToast("Incorrect(attempts remaining $remainingattempts)")
                                    binding.passwordpin.text.clear()

                                } else {
                                    showToast("Seems you forget your previous kindly reset it")
                                    currentAttempts = 0
                                    remainingattempts = 3
                                    binding.pintitle.text = (getString(R.string.enter_your_pingere))
                                    checkpin = false
                                    binding.passwordpin.text.clear()

                                }
                            }
                        }
                    }
                }else if(flag){
                    val pinget = sharedPreferences.getInt("pinpassword", -1)
                     if(pinget == 1){
                         val removepin = binding.passwordpin.text.toString()
                         val getremovepin = sharedPreferences.getString("pinkey", "")
                         if(removepin == getremovepin) {
                             val patternset = sharedPreferences.getBoolean("broadPattern", false)
                             if (patternset) {
                                 sharedPreferences.edit().putBoolean("broadPattern", false).apply()
                                 removeStickyActivity()
                                 showToast("Unlocked")
                                 finish()
                             } else if (!patternset) {
                                 sharedPreferences.edit().putBoolean("Flagcheck", false).apply()
                                 sharedPreferences.edit().putInt("pinpassword", 0).apply()
                                 sharedPreferences.edit().putInt("patternpass", 0).apply()
                                 sharedPreferences.edit().putString("pinkey", "").apply()
                                 stopService()
                                 showToast("Password remove")
                                 navigation.navigateTo(MainActivity::class.java)
                                 finish()
                             }
                         }else {
                             captureAndSavePhoto()
                             binding.passwordpin.text.clear()
                             showToast("Pin Does Not Match")
                         }
                     }
                }

            }
            }

    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun startService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        startService(serviceIntent)
    }
    private fun stopService() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)

    }
    private fun startStickyActivity() {
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            if (!dialogShown) {

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

                val parentView = binding.root.parent as? ViewGroup
                parentView?.removeView(binding.root)

                windowManager.addView(binding.root, layoutParams)
            }
        }

    private fun showCustomDialog() {
        val question = sharedPreferences.getInt("question_selected",-1)
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val textView = TextView(this)
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
        editText.hint = "Enter your answer "
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
                    showToast("Enter your answer please")
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
        val outputOptions: OutputFileOptions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "Intruder_Newer"
            )
            photoFile = File(storageDir, "photo_$timestamp.jpg")
            outputOptions = OutputFileOptions.Builder(photoFile).build()
        } else {
              storageDir = File(Environment.getExternalStorageDirectory(), "Intruder_older")
            photoFile = File(storageDir, "photo_$timestamp.jpg")
            outputOptions = OutputFileOptions.Builder(photoFile).build()
        }

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this@PinPasswordActivity),
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

