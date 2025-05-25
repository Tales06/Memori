package com.example.memori.auth

/**
 * Data class representing the result of a sign-in operation.
 *
 * @property data The user data if the sign-in was successful, or null otherwise.
 * @property error The error message if the sign-in failed, or null otherwise.
 */
data class SignInRes(
    val data: UserData?,
    val error: String?,
)

/**
 * Data class representing user information.
 *
 * @property userId The unique identifier of the user.
 * @property username The display name of the user, or null if not available.
 * @property profilePicture The URL of the user's profile picture, or null if not available.
 */
data class UserData(
    val userId: String,
    val username: String?,
    val profilePicture: String?
)