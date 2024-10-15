package com.az.elib.domain.models

data class UserLocalData (
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val year: String? = null,
    val image : String? = null,
)