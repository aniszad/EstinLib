package com.az.elib.domain.models

import com.google.firebase.Timestamp


data class Comment (
    val id: String,
    val ownerId: String,
    val postId: String,
    val content: String,
    val ownerFullName: String,
    val ownerImage: String? = null,
    val timestamp: Timestamp = Timestamp.now()
)