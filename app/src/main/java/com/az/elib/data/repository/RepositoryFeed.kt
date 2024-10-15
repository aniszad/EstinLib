package com.az.elib.data.repository

import com.az.elib.data.datasource.FirebaseFirestoreFeed
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.domain.models.Comment
import com.google.firebase.firestore.DocumentSnapshot

class RepositoryFeed(private val firestoreFeed: FirebaseFirestoreFeed, private val sharedPreferences: MySharedPreferences) {

    suspend fun getNextPostsBatch(lastVisited: DocumentSnapshot?): Result<List<DocumentSnapshot>> {
        val channel = sharedPreferences.getChannel()
        return firestoreFeed.getNextPostsBatch(lastVisited, channel)
    }

    suspend fun incrementReacts(postId: String) : Result<Boolean> {
        return firestoreFeed.incrementReacts(postId)
    }

    suspend fun addUserIdToReactionList(postId: String, userId:String) : Result<Boolean>{
        return firestoreFeed.addUserIdToReactionList(postId, userId)
    }

    suspend fun decrementReacts(postId: String): Result<Boolean> {
        return firestoreFeed.decrementReacts(postId)
    }

    suspend fun removeUserIdFromReactionList(postId: String, userId: String): Result<Boolean> {
        return firestoreFeed.removeUserIdFromReactionList(postId, userId)
    }

    suspend fun addComment(comment: Comment) {
        return firestoreFeed.addComment(comment)
    }

    fun createCommentRef(): String {
        return firestoreFeed.createCommentRef()
    }

    suspend fun getNextCommentsBatch(lastVisitedDocument: DocumentSnapshot?, postId: String): Result<List<DocumentSnapshot>>{
        return firestoreFeed.getNextCommentsBatch(lastVisitedDocument, postId)
    }


    suspend fun deleteComment(postId: String, commentId: String){
        return firestoreFeed.deleteComment(postId, commentId)
    }

    suspend fun deletePost(postId: String) {
        return firestoreFeed.deletePost(postId)
    }
}