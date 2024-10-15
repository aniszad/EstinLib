package com.az.elib.domain.models

import com.google.firebase.Timestamp

data class Post(
    var id: String,
    var ownerId: String,
    var ownerFirstName: String,
    var ownerLastName: String,
    var ownerEmail: String,
    var ownerYear: String? = null,
    var ownerImage: String? = null,
    var postContent: String,
    var postImageAttachments: List<String>,
    var tag: String? = null,
    var channel: String? = null,
    var postReactCount: Long = 0,
    var postLikesIds: List<String>,
    var timestamp: Timestamp
)