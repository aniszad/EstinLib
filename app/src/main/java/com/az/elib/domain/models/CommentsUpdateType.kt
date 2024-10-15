package com.az.elib.domain.models


sealed class CommentsUpdateType {
    data object Added : CommentsUpdateType()
    object Deleted : CommentsUpdateType()
    object NextBatch : CommentsUpdateType()
    object Initialized : CommentsUpdateType()
}