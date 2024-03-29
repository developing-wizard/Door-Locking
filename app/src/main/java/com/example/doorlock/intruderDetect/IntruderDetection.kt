package com.example.doorlock.intruderDetect


import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.doorlock.R
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivityIntruderDetectionBinding
import com.example.doorlock.homeScreen.MainActivity
import java.io.File


class IntruderDetection : AppCompatActivity() {
    private val binding by lazy {
        ActivityIntruderDetectionBinding.inflate(layoutInflater)
    }
    private lateinit var navigation : NavigateActivity
    private val PERMISSION_REQUEST_READ_IMAGES = 1003
    private val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1002
    private val CAMERA_PERMISSION_REQUEST = 1001
    lateinit var RVintruder: RecyclerView
    lateinit var intruderRVadapter: IntruderRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigation = NavigateActivity(this)
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST
            )
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {

            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)
            }
        }
        loadImages()
        intruderRVadapter.setOnItemClickListener(object : IntruderRvAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, image: File) {

            }

        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })

    }

    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.backintruder -> {
                navigation.navigateTo(MainActivity::class.java)
                finish()

            }

            R.id.switch2 -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        PERMISSION_REQUEST_READ_IMAGES
                    )
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
        when (requestCode) {
            PERMISSION_REQUEST_READ_IMAGES -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                } else {
                    Toast.makeText(

                        this,
                        "Permission denied. Cannot save the image.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                }
                }
            }
        }
    fun getImagesFromFolder(folderPath: String): MutableList<File> {
        val folder = File(folderPath)
        val imageFiles = mutableListOf<File>()
        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile && isImageFile(file)) {
                        imageFiles.add(file)
                    }
                }
            }
        }

        return imageFiles
    }

    fun isImageFile(file: File): Boolean {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        return extension != null && extension.lowercase() in arrayOf("jpg", "jpeg", "png", "gif", "bmp")
    }
    private fun loadImages() {
        val dcimFolderPath: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val intruderFolderPath = "$dcimFolderPath/Intruder_Newer"
        val imageFiles = getImagesFromFolder(intruderFolderPath)
        imageFiles.reversed()
        RVintruder = binding.intruders
        val layoutManager = GridLayoutManager(this, 2)
        RVintruder.layoutManager = layoutManager
        intruderRVadapter = IntruderRvAdapter(imageFiles, this)
        RVintruder.adapter = intruderRVadapter
        intruderRVadapter.notifyDataSetChanged()


        if (imageFiles.isEmpty()) {
            Toast.makeText(this, "No images found in the folder.", Toast.LENGTH_SHORT).show()
        }
    }

}




