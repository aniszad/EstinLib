package com.az.elib.domain.usecases.postfeed

import com.az.elib.data.repository.RepositoryFeed
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class GetPostsBatchUseCase @Inject constructor(
    private val repositoryFeed: RepositoryFeed,
) {
    suspend fun invoke(
        lastVisitedDocument: DocumentSnapshot?
    ): Result<List<DocumentSnapshot>> {
        return try {
            val docsSnapshots =
                repositoryFeed.getNextPostsBatch(lastVisitedDocument).getOrThrow()
            Result.success(docsSnapshots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
