package com.az.elib.domain.usecases.comments

import com.az.elib.data.repository.RepositoryFeed
import javax.inject.Inject


class DeleteCommentUseCase @Inject constructor(
    private val repositoryFeed: RepositoryFeed
){
     suspend fun invoke(postId: String, commentId : String) : Result<String>{
         return try {
             repositoryFeed.deleteComment(postId, commentId)
             Result.success(commentId)
         }catch (e: Exception){
             Result.failure(e)
         }
     }
}