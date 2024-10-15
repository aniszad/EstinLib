package com.az.elib.domain.interfaces

interface OnReactClickListener {
    fun addReaction(postId: String, postPosition:Int)
    fun removeReaction(postId: String, postPosition:Int)
}