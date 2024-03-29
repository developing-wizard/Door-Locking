package com.example.doorlock.activityChanger

import android.content.Context
import android.content.Intent


class NavigateActivity(private val context: Context)  {
    fun navigateTo(targetActivity: Class<*>) {
        val intent = Intent(context, targetActivity)
        context.startActivity(intent)
}
}