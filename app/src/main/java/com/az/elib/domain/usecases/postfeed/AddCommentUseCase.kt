package com.az.elib.domain.usecases.postfeed

import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryFeed
import com.az.elib.domain.models.Comment
import javax.inject.Inject


class AddCommentUseCase @Inject constructor(
    private val feedRepo: RepositoryFeed,
    private val mySharedPreferences: MySharedPreferences
) {
    suspend operator fun invoke(postId: String?, commentContent: String): Result<Comment> {
        return try {
            if (postId == null) throw (Exception("No Post Found"))
            val commentRefId = feedRepo.createCommentRef()
            val userId = mySharedPreferences.getUserId() ?: throw Exception("User not found")
            val userFullName = mySharedPreferences.getFullName() ?: throw Exception("User not found")
            val comment = Comment(id = commentRefId, ownerId = userId ,postId = postId, content = commentContent, ownerFullName = userFullName)
            feedRepo.addComment(comment)
            Result.success(comment)
        }catch(e: Exception){
            Result.failure(e)
        }
    }
}