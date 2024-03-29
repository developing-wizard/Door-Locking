package com.example.doorlock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.doorlock.passwordSelection.PatternPasswordActivity
import com.example.doorlock.passwordSelection.PinPasswordActivity


class UnlockReceiver : BroadcastReceiver() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        }
            val pattern = sharedPreferences.getInt("patternpass",-1)
            val pin = sharedPreferences.getInt("pinpassword",-1)
        if (intent?.action == Intent.ACTION_USER_PRESENT && context != null) {
            if(pattern == 1) {

                sharedPreferences.edit().putBoolean("broadPattern",true).apply()

                val i = Intent(context, PatternPasswordActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(i)
            }else if(pin == 1){
                sharedPreferences.edit().putBoolean("broadPattern",true).apply()
                val i = Intent(context, PinPasswordActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(i)
            }else{

            }
        }



    }
}

