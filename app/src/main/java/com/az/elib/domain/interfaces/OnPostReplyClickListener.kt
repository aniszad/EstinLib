package com.az.elib.domain.interfaces

interface OnPostReplyClickListener {
    fun onReplyClicked(emailAddress: String, subject:String="")
}