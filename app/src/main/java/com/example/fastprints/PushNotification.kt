package com.example.fastprints

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotification : FirebaseMessagingService() {

    //generate notification
    @SuppressLint("UnspecifiedImmutableFlag") //Suppress: Missing `PendingIntent` mutability flag
    private fun generateNotification(title: String, message: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //close all top activities and show this activity

        //only use this activity once therefore, flag one shot
        val pendingIntent = PendingIntent.getActivity(this, Constants.REQUEST_CODE_NOTIFICATION, intent, PendingIntent.FLAG_ONE_SHOT)

        //channel id, channel name (update from android Oreo)
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_print_24) //Set the small icon to use in the notification layouts
            .setAutoCancel(true) //automatically canceled when the user clicks it in the panel.
            .setVibrate(longArrayOf(1000,1000,1000,1000)) //vibrate for 1 sec, rest 1 sec, vibrate 1 sec, rest 1 sec
            .setOnlyAlertOnce(true) //only activate once when is already running
            .setContentIntent(pendingIntent)

        //provide a custom RemoteViews to use instead of the standard one
        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //if version equal or higher than Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(Constants.REQUEST_CODE_NOTIFICATION, builder.build())
    }

    //attach notification created with custom layout
    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.fastprints", R.layout.notification_push)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.appLogo, R.drawable.ic_baseline_print_24)

        return remoteView
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null){
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

}