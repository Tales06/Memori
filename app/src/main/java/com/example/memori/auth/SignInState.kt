package com.example.memori.auth

/**
 * Class representing the state of the sign-in process.
 *
 * @property isSignInSuccessful Indicates whether the sign-in was completed successfully.
 * @property errorMessage Contains an error message if the sign-in failed, or null if there are no errors.
 */
data class SignInState(
    var isSignInSuccessful: Boolean = false,
    val errorMessage: String? = null,
)
