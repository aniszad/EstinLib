package com.az.elib.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.elib.data.local.MySharedPreferences
import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelSettings @Inject constructor(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryUser: RepositoryUser,
    private val mySharedPref: MySharedPreferences
) : ViewModel() {

    private val _sendPasswordChangeEmailResult = MutableStateFlow<Boolean?>(null)
    val sendPasswordChangeEmailResult: StateFlow<Boolean?> = _sendPasswordChangeEmailResult

    private val _notificationsAllowed = MutableStateFlow<Boolean>(true)
    val notificationsAllowed: StateFlow<Boolean> = _notificationsAllowed

    private val _signOutUserResult = MutableStateFlow<Boolean?>(null)
    val signOutUserResult: StateFlow<Boolean?> = _signOutUserResult

    private val _fullName = MutableStateFlow<String>("")
    val fullName: StateFlow<String> = _fullName

    private val _changeImageResult = MutableStateFlow<Result<String>?>(null)
    val changeImageResult: StateFlow<Result<String>?> = _changeImageResult

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    private val _userYear = MutableStateFlow<String?>(null)
    val userYear: StateFlow<String?> = _userYear

    private val _userImage = MutableStateFlow<String?>(null)
    val userImage: StateFlow<String?> = _userImage

    fun sendChangePasswordEmail() = viewModelScope.launch(Dispatchers.IO) {
        _sendPasswordChangeEmailResult.value = repositoryAuth.sendChangePasswordEmail()
    }

    fun updateNotificationAllowed(notificationsAllowed: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            mySharedPref.saveNotificationAllowed(notificationsAllowed)
        }

    fun getNotificationsAllowed() = viewModelScope.launch(Dispatchers.IO) {
        _notificationsAllowed.value = mySharedPref.getNotificationAllowed()
    }

    fun signOutUser() = viewModelScope.launch(Dispatchers.IO) {
        try {
            mySharedPref.clearAll()
            _signOutUserResult.value = repositoryAuth.signOutUser()
        }catch (e : Exception){
            _signOutUserResult.value = false
        }

    }

    fun getUserInfo() = viewModelScope.launch(Dispatchers.IO) {
        _fullName.value = "${mySharedPref.getUserLastName()} ${mySharedPref.getUserFirstName()}"
        _userEmail.value = mySharedPref.getUserEmail()
        _userYear.value = mySharedPref.getUserDetails()
        _userImage.value = mySharedPref.getUserImage()
    }

    fun changeUserImage(image: String) = viewModelScope.launch(Dispatchers.IO) {
        _changeImageResult.value = repositoryUser.changeUserImage(
            image,
            mySharedPref.getUserId() ?: throw Exception("User Not Found")
        )
    }
}