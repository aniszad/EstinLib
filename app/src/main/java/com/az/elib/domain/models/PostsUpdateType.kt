package com.az.elib.domain.models


sealed class PostsUpdateType {
    object NextBatch : PostsUpdateType()
    object Initialized : PostsUpdateType()
}