package com.az.elib.domain.interfaces

import android.net.Uri

interface OnImageSelectedListener {
    fun onImageSelected(imageUri: Uri)
    fun onImageUnselected(imageUri: Uri)
}