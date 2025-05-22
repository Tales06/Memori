package com.example.memori.auth

data class SignInRes(
    val data: UserData?,
    val error: String?,
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePicture: String?
)
