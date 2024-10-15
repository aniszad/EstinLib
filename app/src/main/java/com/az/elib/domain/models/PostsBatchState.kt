package com.az.elib.domain.models


sealed class PostsBatchState {
    data class Success(val posts: List<Post>, val updateType: PostsUpdateType) : PostsBatchState()
    data object Loading : PostsBatchState()
    data class Error(val exception: Throwable) : PostsBatchState()
}