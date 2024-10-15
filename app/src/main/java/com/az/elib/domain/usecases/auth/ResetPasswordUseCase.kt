package com.az.elib.domain.usecases.auth

import com.az.elib.data.repository.RepositoryAuth
import javax.inject.Inject


class ResetPasswordUseCase @Inject constructor(private val repositoryAuth: RepositoryAuth) {
    suspend fun invoke(email:String) : Result<String>{
        return try {
            repositoryAuth.sendResetPasswordEmail(email)
        }catch (e: Exception){
           Result.failure(e)
        }
    }
}