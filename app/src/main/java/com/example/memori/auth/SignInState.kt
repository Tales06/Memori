package com.example.memori.auth

data class SignInState(
    var isSignInSuccessful: Boolean = false,
    val errorMessage: String? = null,
)
