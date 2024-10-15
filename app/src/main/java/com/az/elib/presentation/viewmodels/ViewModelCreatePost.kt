package com.az.elib.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.elib.domain.models.Post
import com.az.elib.domain.usecases.postfeed.CreatePostUserCase
import com.az.elib.domain.usecases.postfeed.SendNotificationUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ViewModelCreatePost @Inject constructor(
    private val createPostUserCase: CreatePostUserCase,
    private val sendNotificationUserCase: SendNotificationUserCase
) : ViewModel() {


    private val _postCreatingResult = MutableStateFlow<Result<Post>?>(null)
    val postCreatingResult: StateFlow<Result<Post>?> = _postCreatingResult

    private val _tag = MutableStateFlow<String?>(null)
    val tag: StateFlow<String?> = _tag

    private val _channel = MutableStateFlow<String?>(null)
    val channel: StateFlow<String?> = _channel

    private val _sendNotificationResult = MutableStateFlow<Result<String>?>(null)
    val sendNotificationResult: StateFlow<Result<String>?> = _sendNotificationResult

    private val _attachList = MutableStateFlow<HashMap<String, Uri>>(hashMapOf())
    val attachList: StateFlow<HashMap<String, Uri>> = _attachList

    fun createPostAndNotification(postContentEditText: EditText) = viewModelScope.launch(Dispatchers.IO) {
        val postCreatingResult = createPostUserCase.invoke(postContentEditText.text.toString(), tag.value, channel.value,_attachList.value)
        Log.e("Notification", "${postCreatingResult.getOrNull() ?: return@launch}")
        _postCreatingResult.value = postCreatingResult
        if (postCreatingResult.isFailure) return@launch
        val sendNotificationResult = sendNotificationUserCase.invoke(postCreatingResult.getOrNull() ?: return@launch)
        _sendNotificationResult.value = sendNotificationResult
    }

    fun compressAndAddFile(requireContext: Context, file: File) = viewModelScope.launch(Dispatchers.IO) {
        val compressedUri = Compressor.compress(requireContext, file) {
            quality(70)
        }
        addToAttachList(file.name, compressedUri.toUri())
    }

    private fun addToAttachList(key: String, uri: Uri) = viewModelScope.launch(Dispatchers.Main) {
        _attachList.value = _attachList.value.toMutableMap().apply { put(key, uri) } as HashMap<String, Uri>
    }

    fun removeFromAttachList(attach: String) {
        _attachList.value = _attachList.value.toMutableMap().apply { remove(attach) } as HashMap<String, Uri>
    }

    fun clearData() {
        _attachList.value = hashMapOf()
    }

    fun setTag(tag: String) {
        _tag.value = tag
    }

    fun setChannel(channel: String) {
        _channel.value = channel
    }

    fun clearPostCreatingResult() {
        _postCreatingResult.value = null
    }

    fun clearSendNotificationResult() {
        _sendNotificationResult.value = null
    }



}