package com.example.doorlock.soundSelection

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doorlock.R
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivitySoundSelectionBinding
import com.example.doorlock.homeScreen.MainActivity
import com.example.doorlock.passwordSelection.PatternPasswordActivity


class SoundSelectionActivity : AppCompatActivity() {
 private val binding by lazy {
     ActivitySoundSelectionBinding.inflate(layoutInflater)
 }
    private lateinit var navigation : NavigateActivity
    private lateinit var  sharedPreferences : SharedPreferences
    private var mediaPlayer = MediaPlayer()
    lateinit var RVsound: RecyclerView
    lateinit var soundRVAdapter: SoundRvAdapter
    lateinit var soundlist: ArrayList<SoundModal>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigation = NavigateActivity(this)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        soundlist = ArrayList()
        RVsound = binding.soundss
        soundlist.add(SoundModal(R.drawable.music, R.raw.voice1))
        soundlist.add(SoundModal(R.drawable.music, R.raw.voice2))
        soundlist.add(SoundModal(R.drawable.music, R.raw.voice4))
        soundlist.add(SoundModal(R.drawable.music, R.raw.voice3))
        soundlist.add(SoundModal(R.drawable.music, R.raw.voice5))

     val layoutManager = GridLayoutManager(this, 2)
        RVsound.layoutManager = layoutManager
        soundRVAdapter = SoundRvAdapter(soundlist, this)
        RVsound.adapter = soundRVAdapter
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })

        soundRVAdapter.setOnItemClickListener(object : SoundRvAdapter.OnItemClickListener
        {
            override fun onItemClick(position: Int, sound: SoundModal) {
                mediaPlayer.reset()
                mediaPlayer = MediaPlayer.create(this@SoundSelectionActivity, sound.resourdeid)
                mediaPlayer.start()
                sharedPreferences.edit().putInt("sound",position).apply()
            }

        })

    }
    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.backSoundbtn -> {
                navigation.navigateTo(MainActivity::class.java)
                finish()

            }
        }
    }
}