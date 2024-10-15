package com.az.elib.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class TimeFormatter {

    fun formatTimestamp(timestamp: Timestamp): String {
        val currentTime = System.currentTimeMillis()
        val timestampMillis = timestamp.toDate().time

        val diffInMillis = currentTime - timestampMillis
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24

        return when {
            diffInDays >= 7 -> {
                val dateFormat = SimpleDateFormat("EEE dd MMM", Locale.getDefault())
                dateFormat.format(timestamp.toDate())
            }
            diffInDays in 1..6 -> {
                "$diffInDays days ago"
            }
            diffInHours in 1..23 -> {
                "$diffInHours hours ago"
            }
            diffInMinutes in 1..59 -> {
                "$diffInMinutes minutes ago"
            }
            else -> {
                "Just now"
            }
        }
    }

}