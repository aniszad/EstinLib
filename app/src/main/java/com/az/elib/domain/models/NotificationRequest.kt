package com.az.elib.domain.models

import com.az.elib.domain.models.Notification

data class NotificationRequest(
    val topic: String,
    val notification: Notification,
    val data : HashMap<String, String>
)
