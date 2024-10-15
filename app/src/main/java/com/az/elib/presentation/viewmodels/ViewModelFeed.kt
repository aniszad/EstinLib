package com.az.elib.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryFeed
import com.az.elib.domain.models.PostsBatchState
import com.az.elib.domain.models.PostsUpdateType
import com.az.elib.domain.usecases.postfeed.AddReactionUserCase
import com.az.elib.domain.usecases.postfeed.DeletePostUseCase
import com.az.elib.domain.usecases.postfeed.GetPostsBatchUseCase
import com.az.elib.domain.usecases.postfeed.RemoveReactionUserCase
import com.az.elib.util.ModelMapper
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFeed @Inject constructor(
    private val repositoryFeed: RepositoryFeed,
    private val getPostsBatchUseCase: GetPostsBatchUseCase,
    private val addReactionUserCase: AddReactionUserCase,
    private val removeReactionUserCase: RemoveReactionUserCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val mySharedPreferences: MySharedPreferences

) : ViewModel() {

    private var lastVisitedDocument: DocumentSnapshot? = null
    private val modelMapper = ModelMapper()


    private val _postsBatch = MutableStateFlow<PostsBatchState>(PostsBatchState.Loading)
    val postsBatch: StateFlow<PostsBatchState> = _postsBatch

    private val _postFromNotification = MutableStateFlow<String?>(null)
    val postFromNotification: StateFlow<String?> = _postFromNotification

    private val _addReactionResult = MutableStateFlow<Result<Boolean>?>(null)
    val addReactionResult: StateFlow<Result<Boolean>?> = _addReactionResult

    private val _removeReactionResult = MutableStateFlow<Result<Boolean>?>(null)
    val removeReactionResult: StateFlow<Result<Boolean>?> = _removeReactionResult

    private val _deletePostResult = MutableStateFlow<Result<String>?>(null)
    val deletePostResult: StateFlow<Result<String>?> = _deletePostResult




    fun getNextPostsBatch() = viewModelScope.launch(Dispatchers.IO) {
        val result =  getPostsBatchUseCase.invoke(lastVisitedDocument)
        val initialized = lastVisitedDocument==null
        lastVisitedDocument = result.getOrNull()?.lastOrNull()
        val postsList = modelMapper.mapDocumentsToPostsList(result.getOrNull() ?: emptyList())
        _postsBatch.value = if (result.isSuccess){
            PostsBatchState.Success(
                if (initialized) postsList else (_postsBatch.value as? PostsBatchState.Success)?.posts?.toMutableList()
                    ?.plus((postsList)) ?: emptyList(),
                if (initialized)  PostsUpdateType.Initialized  else PostsUpdateType.NextBatch
            )
        }else{
            PostsBatchState.Error(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }

    fun scrollToPostFromNotification(postId: String) {
        _postFromNotification.value = postId
    }

    fun addReaction(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _addReactionResult.value = addReactionUserCase.invoke(postId)
    }


    fun removeReaction(postId: String) = viewModelScope.launch(Dispatchers.IO) {
        _removeReactionResult.value = removeReactionUserCase.invoke(postId)
    }

    fun deletePost(postId: String) = viewModelScope.launch(Dispatchers.IO){
        _deletePostResult.value = deletePostUseCase.invoke(postId)
    }

    fun getUserId(): String {
        return mySharedPreferences.getUserId() ?: ""
    }

    fun clearAddReactionResult() {
        _addReactionResult.value = null
    }

    fun clearRemoveReactionResult() {
        _removeReactionResult.value = null
    }

    fun clearDeletePostResult() {
        _deletePostResult.value = null
    }


}