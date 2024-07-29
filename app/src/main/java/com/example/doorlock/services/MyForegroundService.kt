package com.example.doorlock.services


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.doorlock.R
import com.example.doorlock.homeScreen.MainActivity
import com.example.doorlock.receiver.UnlockReceiver

class MyForegroundService : Service() {
    private val notificationid = 1
    private val channel_id = "MyForegroundServiceChannel"
    private  val unlockReceiver = UnlockReceiver()
    override fun onCreate() {
        super.onCreate()
        startForeground(notificationid, createNotification())
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(unlockReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        unregisterReceiver(unlockReceiver)
    }


    private fun createNotification(): Notification {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channel_id,
                "My Foreground Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channel_id)
            .setContentTitle("Door Lock")
            .setContentText("Running in background")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.baseline_notifications_active)
        return builder.build()

    }

}
