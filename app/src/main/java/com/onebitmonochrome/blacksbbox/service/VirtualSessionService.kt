package com.onebitmonochrome.blacksbbox.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.onebitmonochrome.blacksbbox.R
import com.onebitmonochrome.blacksbbox.view.main.MainActivity

class VirtualSessionService : Service() {
    override fun onCreate() {
        super.onCreate()
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Virtual session", NotificationManager.IMPORTANCE_LOW)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEEP_RUNNING_KEY, true)) {
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIFICATION_ID, notification())
        return START_STICKY
    }

    private fun notification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_view)
        .setContentTitle(getString(R.string.app_name))
        .setContentText("Virtual session active")
        .setContentIntent(PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", PendingIntent.getService(this, 2, Intent(this, VirtualSessionService::class.java).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        .build()

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.onebitmonochrome.blacksbbox.action.START_SESSION"
        private const val ACTION_STOP = "com.onebitmonochrome.blacksbbox.action.STOP_SESSION"
        private const val KEEP_RUNNING_KEY = "keep_virtual_apps_running"
        private const val CHANNEL_ID = "virtual_session"
        private const val NOTIFICATION_ID = 4101
    }
}
