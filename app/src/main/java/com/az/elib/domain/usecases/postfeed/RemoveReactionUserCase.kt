package com.az.elib.domain.usecases.postfeed

import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFeed
import javax.inject.Inject

class RemoveReactionUserCase @Inject constructor(private val repositoryFeed: RepositoryFeed, private val repositoryAuth: RepositoryAuth) {
    suspend fun invoke(postId: String) : Result<Boolean> {
        return try{
            val userId = repositoryAuth.getCurrentUserId() ?: throw Exception("User not found")
            repositoryFeed.decrementReacts(postId).getOrThrow()
            repositoryFeed.removeUserIdFromReactionList(postId, userId).getOrThrow()
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}