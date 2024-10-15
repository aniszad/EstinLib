package com.az.elib.data.datasource.retrofit

import com.az.elib.domain.models.FcmResponse
import com.az.elib.domain.models.NotificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitCloudMessagingAPI {
    @POST("https://fcm.googleapis.com/v1/projects/elib-6128f/messages:send")
    suspend fun sendNotification(@Body notificationRequest: HashMap<String, NotificationRequest>): Response<FcmResponse>
}