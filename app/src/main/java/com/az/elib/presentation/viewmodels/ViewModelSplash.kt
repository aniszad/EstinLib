package com.az.elib.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.az.elib.data.repository.RepositoryAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class ViewModelSplash @Inject constructor(private val repositoryAuth: RepositoryAuth) : ViewModel(){
    private val _isUserSignedIn = MutableStateFlow<Result<Boolean>?>(null)
    val isUserSignedIn: StateFlow<Result<Boolean>?> = _isUserSignedIn

    fun checkIfUserSignedIn() {
        _isUserSignedIn.value = repositoryAuth.isUserSignedIn()
    }
}