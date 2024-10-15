package com.az.elib.data.datasource

import android.util.Log
import com.az.elib.data.datasource.retrofit.RetrofitCloudMessagingAPI
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.domain.models.Notification
import com.az.elib.domain.models.NotificationRequest
import com.az.elib.domain.models.Post
import com.az.elib.util.NotificationLauncher
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirebaseFCM : FirebaseMessagingService() {
    val fcm = Firebase.messaging
    private lateinit var notificationLauncher : NotificationLauncher
    private val mySharedPreferences : MySharedPreferences by lazy {
        MySharedPreferences(this@FirebaseFCM)
    }


    init {
        fcm.subscribeToTopic("posts").addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.e("subscribed", "subscribed to posts")
            }else{
                Log.e("subscribed", "failed to subscribe to posts")
            }

        }

    }
    suspend fun sendNotificationToDevices(
        post: Post,
        retrofitCloudMessagingAPI: RetrofitCloudMessagingAPI
    ): Result<String> {
        Log.e("Notification", "$post")
        val response = retrofitCloudMessagingAPI.sendNotification(
            hashMapOf(
                "message" to NotificationRequest(
                    topic="posts",
                    notification = Notification(
                        title = post.ownerFirstName + " " + post.ownerLastName,
                        body = post.postContent
                    ),
                    data = hashMapOf("postId" to (post.id ?: ""))
                )
            )
        )
        return if (response.isSuccessful) {
            Log.e("Notification", "Notification sent, $response")
            Result.success("Notification sent")
        } else {
            Log.e("Notification err", response.toString())
            Log.e("Notification", "Notification failed")
            Result.failure(Exception(response.message()))
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationsAllowed = mySharedPreferences.getNotificationAllowed()
        Log.e("notifi_rec", remoteMessage.data["postId"] as String)
        try {
            if (notificationsAllowed){
                notificationLauncher = NotificationLauncher(this@FirebaseFCM)
                notificationLauncher.startNotification(
                    " ${remoteMessage.notification?.title} has posted",
                    remoteMessage.notification?.body ?:"",
                    remoteMessage.data["postId"] as String
                )
            }
        }catch (e: Exception){
            Log.e("notifi_err", remoteMessage.data.toString())

        }

    }
}