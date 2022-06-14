package com.example.segundoevalucacion.notificacion

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.segundoevalucacion.ChatActivity
import com.example.segundoevalucacion.MainChatActivity
import com.example.segundoevalucacion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random


/**
 * Notificacion
 *
 * @constructor Create empty Notificacion
 */
class Notificacion : FirebaseMessagingService(){



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {

            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
            val chatId = map["chatId"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                try {
                    createOreoNotification(title!!, message!!, hisId!!,  chatId!!)
                    Log.d("TOKEN>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",hisId + " **"+
                            "***"+message+"***"+chatId+"***"+title)
                }catch (e: Exception){
                    Log.d("Error>>>>>>>>>>>>>>>>>>>>>>>>>>>>","createOreoNotification")
                }

            }
            else{
                try {
                    createNormalNotification(title!!, message!!, hisId!!,  chatId!!)
                    Log.d("TOKEN>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",hisId + " **"+
                            "***"+message+"***"+chatId+"***"+title)
                }catch (e: Exception){
                    Log.d("Error>>>>>>>>>>>>>>>>>>>>>>>>>>>>","createNormalNotification")
                }

            }

        }


    }


    private fun updateToken(token: String) {

        val databaseReference =
            FirebaseDatabase.getInstance().getReference("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!)
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        databaseReference.updateChildren(map)

    }

    private fun createNormalNotification(
        title: String,
        message: String,
        hisId: String,
        chatId: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, ChatActivity::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this)
            .setChannelId(AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.purple_200, null))
            .setSound(uri)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(java.util.Random().nextInt(85 - 65), builder)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisId: String,
        chatId: String
    ) {

        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, ChatActivity::class.java)

        intent.putExtra("hisId", hisId)
        intent.putExtra("chatId", chatId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this)
            .setChannelId(AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.purple_200, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }


}