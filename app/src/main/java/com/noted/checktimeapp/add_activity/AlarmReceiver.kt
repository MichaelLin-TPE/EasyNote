package com.noted.checktimeapp.add_activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.noted.checktimeapp.MainActivity
import com.noted.checktimeapp.R

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    private lateinit var notificationManager: NotificationManager

    companion object{
        private const val CHANNEL_ID = "1"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null){
            Log.i("Michael","context == null or intent == null 無法推播")
            return
        }
        Log.i("Michael","準備推播")

        this.context = context
        //為了安卓 8 以上的創見頻道
        createNotificationChannel()
        val bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.app_icon)
        val fullScreenIntent = Intent(context,MainActivity::class.java)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val fullScreenPendingIntent = PendingIntent.getActivity(context,0,fullScreenIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("事件提醒")
            .setContentText(intent.getStringExtra("event"))
            .setContentIntent(fullScreenPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setChannelId(CHANNEL_ID)
            .setFullScreenIntent(fullScreenPendingIntent,true)

        notificationManager.notify(0,notificationBuilder.build())
    }
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = context.getString(R.string.channel)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}