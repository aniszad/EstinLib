package com.az.elib.domain.models

data class User (
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val suspended : Boolean,
    val year: String? = null,
    val image : String? = null,
)