package com.az.elib.domain.usecases.postfeed

import com.az.elib.data.repository.RepositoryFeed
import javax.inject.Inject


class DeletePostUseCase @Inject constructor(
    private val repositoryFeed: RepositoryFeed
){
     suspend fun invoke(postId: String) : Result<String>{
         return try {
             repositoryFeed.deletePost(postId)
             Result.success(postId)
         }catch (e: Exception){
             Result.failure(e)
         }
     }
}