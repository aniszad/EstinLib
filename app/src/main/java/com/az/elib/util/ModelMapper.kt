package com.az.elib.util

import com.az.elib.domain.models.Comment
import com.az.elib.domain.models.Post
import com.az.elib.domain.models.UserLocalData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class ModelMapper {

    fun mapUserToUserEntity(data: MutableMap<String, Any>?): UserLocalData? {
        return if (data != null) {
            UserLocalData(
                id = data["id"] as String,
                firstName = data["firstName"] as String,
                lastName = data["lastName"] as String,
                email = data["email"] as String,
                year = data["year"] as String,
                image = data["image"] as String?
            )
        }else{
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun mapDocumentsToPostsList(documents: List<DocumentSnapshot>): List<Post> {
        val postsList = mutableListOf<Post>()
        for (document in documents){
            if (document.data == null) continue  // skip the document it has no data
            val post = Post(
                id = document.get("id") as String,
                ownerId = document.get("ownerId") as String,
                ownerEmail = document.get("ownerEmail") as String,
                ownerFirstName = document.get("ownerFirstName") as String,
                ownerImage = document.get("ownerImage") as String?,
                ownerLastName = document.get("ownerLastName") as String,
                ownerYear = document.get("ownerYear") as String?,
                postContent = document.get("postContent") as String,
                postImageAttachments = (document.get("postImageAttachments") as? List<String>) ?: emptyList(),
                tag = document.get("tag") as String?,
                postReactCount = document.get("postReactCount") as Long,
                postLikesIds = (document.get("postLikesIds") as? List<String>) ?: emptyList(),
                timestamp =document.get("timestamp") as Timestamp

            )
            postsList.add(post)
        }
        return postsList
    }

    fun mapDocumentsToCommentsList(documents: List<DocumentSnapshot>) : List<Comment>{
        val commentsList = mutableListOf<Comment>()
        for (document in documents){
            if (document.data == null) continue  // skip the document it has no data
            val comment = Comment(
                id = document.get("id") as String,
                ownerId = document.get("ownerId") as String,
                postId = document.get("postId") as String,
                content = document.get("content") as String,
                ownerFullName = document.get("ownerFullName") as String,
                timestamp =document.get("timestamp") as Timestamp

            )
            commentsList.add(comment)
        }
        return commentsList
    }
}