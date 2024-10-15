package com.az.elib.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.domain.usecases.auth.ResetPasswordUseCase
import com.az.elib.domain.usecases.auth.SignInUserCase
import com.az.elib.domain.usecases.auth.SignInWithGoogleUserCase
import com.az.elib.domain.usecases.auth.SignUpUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelAuth @Inject constructor(
    private val repositoryAuth: RepositoryAuth,
    private val signUpUseCase: SignUpUseCase,
    private val signInUserCase: SignInUserCase,
    private val signInUserCaseWithGoogle: SignInWithGoogleUserCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _signInResult = MutableStateFlow<Result<String>?>(null)
    val signInResult: StateFlow<Result<String>?> = _signInResult

    private val _signInWithGoogleResult = MutableStateFlow<Result<String>?>(null)
    val signInWithGoogleResult: StateFlow<Result<String>?> = _signInWithGoogleResult

    private val _signUpResult = MutableStateFlow<Result<String>?>(null)
    val signUpResult: StateFlow<Result<String>?> = _signUpResult

    private val _verificationEmailResult = MutableStateFlow<Result<Boolean>?>(null)
    val verificationEmailResult: StateFlow<Result<Boolean>?> = _verificationEmailResult

    private val _sendResetPswEmail = MutableStateFlow<Result<String>?>(null)
    val sendResetPswEmail: StateFlow<Result<String>?> = _sendResetPswEmail

    private val _userFirstName = MutableStateFlow<String?>(null)
    private val userFirstName: StateFlow<String?> = _userFirstName

    private val _userLastName = MutableStateFlow<String?>(null)
    private val userLastName: StateFlow<String?> = _userLastName

    private val _userEmail = MutableStateFlow<String?>(null)
    private val userEmail: StateFlow<String?> = _userEmail

    private val _userPassword = MutableStateFlow<String?>(null)
    private val userPassword: StateFlow<String?> = _userPassword

    private val _userYear = MutableStateFlow<String?>(null)
    private val userYear: StateFlow<String?> = _userYear


    private val _userSignInEmail = MutableStateFlow<String?>(null)
    private val userSignInEmail: StateFlow<String?> = _userSignInEmail

    private val _userSignInPassword = MutableStateFlow<String?>(null)
    private val userSignInPassword: StateFlow<String?> = _userSignInPassword

    fun signIn() = viewModelScope.launch(Dispatchers.IO) {
        _signInResult.value = signInUserCase.invokeSignInWithEmailAndPassword(userSignInEmail.value, userSignInPassword.value)
    }

    fun signUp() = viewModelScope.launch(Dispatchers.IO) {
        _signUpResult.value = signUpUseCase.invoke(
            userFirstName.value,
            userLastName.value,
            userEmail.value,
            userPassword.value,
            userYear.value
        )
    }

    fun sendVerificationEmail() = viewModelScope.launch(Dispatchers.IO) {
        _verificationEmailResult.value = repositoryAuth.sendVerificationEmail()
    }

    fun signInWithGoogle(idToken: GoogleSignInAccount) = viewModelScope.launch(Dispatchers.IO) {
        _signInWithGoogleResult.value = signInUserCaseWithGoogle.invokeSignInWithGoogle(idToken)
    }


    fun sendResetPasswordEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        _sendResetPswEmail.value = resetPasswordUseCase.invoke(email)
    }

    fun setYear(studentYear: String) {
        _userYear.value = studentYear
    }

    fun setUserFirstName(text : String) {
        _userFirstName.value = text
    }
    fun setUserLastName(text:String) {
        _userLastName.value = text
    }
    fun setUserEmail(text:String) {
        _userEmail.value = text

    }
    fun setUserPassword(text:String) {
        _userPassword.value = text

    }
    fun setSignInEmail(text:String){
        _userSignInEmail.value = text
    }
    fun setSignInPassword(text:String){
        _userSignInPassword.value = text
    }

    fun clearSendResetPswEmail() {
        _sendResetPswEmail.value = null
    }

    fun clearSignUpResult() {
        _signUpResult.value = null
    }

    fun clearVerificationEmailResult() {
        _verificationEmailResult.value = null
    }

    fun clearSignInWithGoogleResult() {
        _signInWithGoogleResult.value = null
    }

    fun clearSignInResult() {
        _signInResult.value = null
    }
}