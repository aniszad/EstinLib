package com.az.elib.domain.usecases.auth

import android.util.Log
import com.az.elib.data.repository.RepositoryAuth
import com.az.elib.data.repository.RepositoryFirestoreAuth
import com.az.elib.util.InputChecker
import javax.inject.Inject
import kotlin.random.Random

class SignUpUseCase @Inject constructor(
    private val repositoryAuth: RepositoryAuth,
    private val repositoryFirestore: RepositoryFirestoreAuth,
    private val inputChecker: InputChecker,
) {
    suspend fun invoke(
        userFirstName: String?,
        userLastName: String?,
        userEmail: String?,
        userPsw: String?,
        userYear: String?,
        ): Result<String> {
        var authResult: Result<String>? = null
        return try {
            userFirstName?.trim()
            userLastName?.trim()
            userEmail?.trim()
            val firstname = if (userFirstName?.isNotBlank() == true) userFirstName else throw Exception("Please enter a valid first name")
            val lastName = if (userLastName?.isNotBlank() == true) userLastName else throw Exception("Please enter a valid first name")
            val year = userYear ?: throw Exception("Please enter a valid year")
            val email = inputChecker.extractEmail(userEmail)
            val password = inputChecker.extractPsw(userPsw)

            val duplicateExists = repositoryFirestore.checkUsernameDuplicate(email)
            if (duplicateExists) {
                throw Exception("Username already exists")
            }
            authResult = repositoryAuth.signUp(email, password)
            val userId = authResult.getOrThrow() // Ensure a successful Auth result

            val dbUserResult = repositoryFirestore.createUser(
                userId,
                firstName = firstname,
                lastName = lastName,
                email = email,
                password=password,
                userYear = year,
                image = getRandomColor()
            )
            dbUserResult.getOrThrow() // Ensure a successful Firestore result
            Log.e("Sign up", "db : $dbUserResult , auth : $authResult")

            Result.success("User created")

        } catch (e: Exception) {
            if (authResult?.isSuccess == true) { // Check if Auth succeeded
                repositoryAuth.deleteUser() // Rollback Auth
            }
            Result.failure(e) // Preserve original exception
        }
    }
    private fun getRandomColor(): String {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)

        return String.format("#%02X%02X%02X", red, green, blue)
    }

}