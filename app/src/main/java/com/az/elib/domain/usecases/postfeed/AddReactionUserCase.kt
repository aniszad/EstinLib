package com.az.elib.domain.usecases.postfeed

import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFeed
import javax.inject.Inject

class AddReactionUserCase @Inject constructor(private val repositoryFeed: RepositoryFeed, private val repositoryAuth: RepositoryAuth) {
    suspend fun invoke(postId: String) : Result<Boolean> {
        return try{
            val userId = repositoryAuth.getCurrentUserId() ?: throw Exception("User not found")
            repositoryFeed.incrementReacts(postId).getOrThrow()
            repositoryFeed.addUserIdToReactionList(postId, userId).getOrThrow()
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}