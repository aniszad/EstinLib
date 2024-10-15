package com.az.elib.domain.usecases.postfeed

import android.util.Log
import com.az.elib.data.datasource.FirebaseFCM
import com.az.elib.data.datasource.retrofit.RetrofitCloudMessagingAPI
import com.az.elib.domain.models.Post
import javax.inject.Inject

class SendNotificationUserCase @Inject constructor(
    private val firebaseFCM: FirebaseFCM,
    private val retrofitCloudMessagingAPI: RetrofitCloudMessagingAPI
) {
    suspend fun invoke(post : Post) : Result<String> {
        return try {
            val sendNotificationResult = firebaseFCM.sendNotificationToDevices(post, retrofitCloudMessagingAPI)
            sendNotificationResult
        }catch (e : Exception){
            Log.e("Notificationerr1", e.stackTraceToString())
            Result.failure(e)
        }
    }


}