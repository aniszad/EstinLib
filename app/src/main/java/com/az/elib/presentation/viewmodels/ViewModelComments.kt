package com.az.elib.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.elib.domain.models.Comment
import com.az.elib.domain.models.CommentsBatchState
import com.az.elib.domain.models.CommentsUpdateType
import com.az.elib.domain.usecases.comments.DeleteCommentUseCase
import com.az.elib.domain.usecases.postfeed.AddCommentUseCase
import com.az.elib.domain.usecases.postfeed.GetCommentsBatchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelComments @Inject constructor(
    private val addCommentUserCase: AddCommentUseCase,
    private val deleteCommentUserCase: DeleteCommentUseCase,
    private val getCommentsBatchUseCase: GetCommentsBatchUseCase,
) : ViewModel(){

    private val _addCommentResult = MutableStateFlow<CommentsBatchState>(CommentsBatchState.Loading)
    val addCommentResult: StateFlow<CommentsBatchState> = _addCommentResult

    private val _deleteCommentResult = MutableStateFlow<CommentsBatchState>(CommentsBatchState.Loading)
    val deleteCommentResult: StateFlow<CommentsBatchState> = _deleteCommentResult

    private val _commentsBatch = MutableStateFlow<CommentsBatchState>(CommentsBatchState.Loading)
    val commentsBatch: StateFlow<CommentsBatchState> = _commentsBatch

    private val _currentCommentsPostId = MutableStateFlow<String?>(null)
    val currentCommentsPostId: StateFlow<String?> = _currentCommentsPostId

    fun getNextCommentsBatch() = viewModelScope.launch(Dispatchers.IO){
        _commentsBatch.value = getCommentsBatchUseCase.invoke(currentCommentsPostId.value)
    }

    fun addComment(commentContent: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = addCommentUserCase.invoke(currentCommentsPostId.value, commentContent)
        if (result.isSuccess) {
            val newComment = result.getOrNull()
            val currentList = (_commentsBatch.value as? CommentsBatchState.Success)?.comments?.toMutableList() ?: mutableListOf()
            newComment?.let { currentList.add(0, it) }  // Add new comment to the top
            _commentsBatch.value = CommentsBatchState.Success(currentList, CommentsUpdateType.Added)
        } else {
            _commentsBatch.value = result.exceptionOrNull()?.let { CommentsBatchState.Error(it) } ?: CommentsBatchState.Loading
        }
    }

    fun setPostIdForComments(postId: String) {
        _currentCommentsPostId.value = postId
    }



    fun deleteComment(postId: String, comment: Comment) = viewModelScope.launch(Dispatchers.IO) {
        val result = deleteCommentUserCase.invoke(postId, comment.id)
        if (result.isSuccess) {
            val currentList = (_commentsBatch.value as? CommentsBatchState.Success)?.comments?.toMutableList() ?: mutableListOf()
            currentList.removeAll { it.id == comment.id }  // Remove the comment with matching ID
            _commentsBatch.value = CommentsBatchState.Success(currentList, CommentsUpdateType.Deleted)
        } else {
            _commentsBatch.value = result.exceptionOrNull()?.let { CommentsBatchState.Error(it) } ?: CommentsBatchState.Loading
        }
    }

}