package com.example.doorlock.doorThemeSelection

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doorlock.R
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.homeScreen.MainActivity

class DoorThemesActivity : AppCompatActivity() {
    lateinit var doorRv: RecyclerView
    lateinit var doorRVAdapter: DoorThemeAdapter
    lateinit var doorList: ArrayList<DoorThemeModal>
    private lateinit var navigation: NavigateActivity
    private lateinit var  sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_door_themes)
        navigation = NavigateActivity(this)
        doorList = ArrayList()
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        doorRv = findViewById(R.id.idRVCourses)
        doorList.add(DoorThemeModal(R.drawable.door1))
        doorList.add(DoorThemeModal(R.drawable.door2))
        doorList.add(DoorThemeModal(R.drawable.door3))
        doorList.add(DoorThemeModal(R.drawable.door4))
        doorList.add(DoorThemeModal(R.drawable.door5))
        doorList.add(DoorThemeModal(R.drawable.door6))
        val layoutManager = GridLayoutManager(this, 2)
        doorRv.layoutManager = layoutManager
        doorRVAdapter = DoorThemeAdapter(doorList, this)
        doorRv.adapter = doorRVAdapter
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })
        doorRVAdapter.setOnItemClickListener(object : DoorThemeAdapter.OnItemClickListener
        {
            override fun onItemClick(position: Int, doors: DoorThemeModal) {
            }

        })
    }
    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.backdoortheme -> {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        }
    }
}
