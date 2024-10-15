package com.az.elib.domain.models

sealed class CommentsBatchState {
    data class Success(val comments: List<Comment>, val updateType: CommentsUpdateType) : CommentsBatchState()
    object Loading : CommentsBatchState()
    data class Error(val exception: Throwable) : CommentsBatchState()
}