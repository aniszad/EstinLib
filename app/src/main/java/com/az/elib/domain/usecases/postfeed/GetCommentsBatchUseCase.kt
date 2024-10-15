package com.az.elib.domain.usecases.postfeed

import android.util.Log
import com.az.elib.data.repository.RepositoryFeed
import com.az.elib.domain.models.CommentsBatchState
import com.az.elib.domain.models.CommentsUpdateType
import com.az.elib.util.ModelMapper
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class GetCommentsBatchUseCase @Inject constructor(
    private val repositoryFeed: RepositoryFeed
) {
    private var lastVisitedDocument: DocumentSnapshot? = null
    private var lastVisitedPostId : String? = null
    private val modelMapper = ModelMapper()

    suspend fun invoke(postId : String?): CommentsBatchState {
        return try {
            val initialized = lastVisitedPostId != postId
            Log.e("CommentsBatchState", "$lastVisitedPostId, $postId")

            if (postId == null) throw IllegalArgumentException("postId is null")
            if (lastVisitedPostId != postId) {
                lastVisitedDocument = null
            }
            val docsSnapshots = repositoryFeed.getNextCommentsBatch(lastVisitedDocument, postId).getOrThrow()
            val docsList = modelMapper.mapDocumentsToCommentsList(docsSnapshots)
            lastVisitedDocument = docsSnapshots.lastOrNull()
            lastVisitedPostId = postId
            Log.e("CommentsBatchState", initialized.toString())
            if (initialized){
                CommentsBatchState.Success(docsList, CommentsUpdateType.Initialized)
            }else{
                CommentsBatchState.Success(docsList, CommentsUpdateType.NextBatch)
            }
        } catch (e: Exception) {
            CommentsBatchState.Error(e)
        }
    }

}
