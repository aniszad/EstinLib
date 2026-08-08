package com.az.elib.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
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

    private val _attachList = MutableStateFlow<List<Pair<String, Uri>>>(emptyList())
    val attachList: StateFlow<List<Pair<String, Uri>>> = _attachList

    fun createPostAndNotification(content: String) = viewModelScope.launch(Dispatchers.IO) {
        val attachmentsMap = HashMap<String, Uri>()
        _attachList.value.forEach { attachmentsMap[it.first] = it.second }

        val postCreatingResult = createPostUserCase.invoke(content, tag.value, channel.value, attachmentsMap)
        _postCreatingResult.value = postCreatingResult
        if (postCreatingResult.isSuccess) {
            val sendNotificationResult = sendNotificationUserCase.invoke(postCreatingResult.getOrThrow())
            _sendNotificationResult.value = sendNotificationResult
        }
    }

    fun addFiles(context: Context, uris: List<Uri>) = viewModelScope.launch(Dispatchers.IO) {
        uris.forEachIndexed { index, uri ->
            try {
                val extension = context.contentResolver.getType(uri)?.substringAfterLast("/") ?: "jpg"
                val fileName = "img_${System.currentTimeMillis()}_${index}.$extension"
                
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, fileName)
                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }
                
                val compressedFile = Compressor.compress(context, tempFile) {
                    quality(70)
                }
                
                addToAttachList(fileName, compressedFile.toUri())
                tempFile.delete() // Clean up temp file
            } catch (e: Exception) {
                Log.e("ViewModelCreatePost", "Error adding file: ${e.message}")
            }
        }
    }

    private fun addToAttachList(key: String, uri: Uri) = viewModelScope.launch(Dispatchers.Main) {
        val currentList = _attachList.value.toMutableList()
        currentList.add(Pair(key, uri))
        _attachList.value = currentList
    }

    fun removeFromAttachList(fileName: String) {
        val currentList = _attachList.value.toMutableList()
        currentList.removeAll { it.first == fileName }
        _attachList.value = currentList
    }

    fun clearData() {
        _attachList.value = emptyList()
        _tag.value = null
        _channel.value = null
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