package com.az.elib.domain.interfaces

import com.az.elib.domain.models.Comment

interface OnDeleteCommentClickListener {
    fun onDeleteCommentClickListener(comment: Comment)
}