package com.example.doorlock.wallpaperSelection


import android.app.WallpaperManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doorlock.R
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivityWallpaperThemesBinding
import com.example.doorlock.homeScreen.MainActivity

class WallpaperThemesActivity : AppCompatActivity() {
    private lateinit var navigation: NavigateActivity
    lateinit var RVwallpaper: RecyclerView
    lateinit var wallRVAdapter: WallpaperRVAdapter
    lateinit var walllpaperlist: ArrayList<WallpaperModal>
    private val biniding by lazy {
        ActivityWallpaperThemesBinding.inflate(layoutInflater)
    }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(biniding.root)
        navigation = NavigateActivity(this)
        walllpaperlist = ArrayList()
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        RVwallpaper = biniding.wallpaper
        walllpaperlist.add(WallpaperModal(R.drawable.cars))
        walllpaperlist.add(WallpaperModal(R.drawable.black_rose))
        walllpaperlist.add(WallpaperModal(R.drawable.flower))
        walllpaperlist.add(WallpaperModal(R.drawable.beleive))
        val layoutManager = GridLayoutManager(this, 2)
        RVwallpaper.layoutManager = layoutManager
        wallRVAdapter = WallpaperRVAdapter(walllpaperlist, this)
        RVwallpaper.adapter = wallRVAdapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })
        wallRVAdapter.setOnItemClickListener(object : WallpaperRVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, wallpaper: WallpaperModal) {
                val editor = sharedPreferences.edit()
                editor.putInt("wallpaperindex", position)
                editor.apply()
                val wallpaperManager = WallpaperManager.getInstance(this@WallpaperThemesActivity)
                val selectedImageResourceId = wallpaper.ivwallpaper
                wallpaperManager.setResource(selectedImageResourceId, WallpaperManager.FLAG_SYSTEM)
                wallpaperManager.setResource(selectedImageResourceId, WallpaperManager.FLAG_LOCK)
                Toast.makeText(
                    this@WallpaperThemesActivity,
                    "Wallpaper Set Successfully",
                    Toast.LENGTH_LONG
                ).show()
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })
    }

    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.backwallpapertheme -> {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        }
    }
}