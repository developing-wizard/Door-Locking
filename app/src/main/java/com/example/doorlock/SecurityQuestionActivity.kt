package com.example.doorlock

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.doorlock.activityChanger.NavigateActivity
import com.example.doorlock.databinding.ActivitySecurityQuestionBinding
import com.example.doorlock.homeScreen.MainActivity


class SecurityQuestionActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySecurityQuestionBinding.inflate(layoutInflater)
    }
    private lateinit var navigation : NavigateActivity
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var answer : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigation = NavigateActivity(this)
        val items = listOf("Mothers Name?", "Fathers Name?", "Place of birth?", "Favourite pet?")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val textView = view as? TextView
                sharedPreferences.edit().putInt("question_selected",position).apply()
                textView?.setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val btntext = sharedPreferences.getString("secutirtyAnser","")
        if(btntext == "")
        {
            binding.donebtn.text = "Done"
        }else{
            val value = sharedPreferences.getInt("question_selected",-1)
           binding.spinner.setSelection(value)
            binding.donebtn.text = "Update"
        }
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
        })
    }

    fun OnClickMethod(view: View) {
        when (view.id) {
            R.id.back -> {
                navigation.navigateTo(MainActivity::class.java)
                finish()
            }
            R.id.donebtn->{
                val securityanswer = binding.answer.text.toString()
                answer = securityanswer
               sharedPreferences.edit().putString("secutirtyAnser",answer).apply()
                if(securityanswer.isEmpty()){
                    Toast.makeText(this,"Please enter your answer",Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this,"Recovery answer set",Toast.LENGTH_SHORT).show()
                    navigation.navigateTo(MainActivity::class.java)
                    finish()
                }
            }
        }
    }
}
