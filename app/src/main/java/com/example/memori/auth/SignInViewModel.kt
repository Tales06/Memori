package com.example.memori.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel responsible for managing the state of the sign-in process.
 * It handles user authentication state, loading indicators, and user data.
 */
class SignInViewModel : ViewModel() {

    // Mutable state flow for the sign-in state
    private val _state = MutableStateFlow(SignInState())

    /** Publicly exposed state flow for observing the sign-in state. */
    val state: StateFlow<SignInState> = _state.asStateFlow()

    // Mutable state flow for the loading state
    private val _loading = MutableStateFlow(false)

    /** Publicly exposed state flow for observing the loading state. */
    val isLoading: StateFlow<Boolean> = _loading.asStateFlow()

    // Mutable state flow for the user data
    private val _userData = MutableStateFlow<UserData?>(null)

    /** Publicly exposed state flow for observing the signed-in user data. */
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    /**
     * Marks the start of the sign-in process by setting the loading state to true.
     */
    fun onSignInStart() {
        _loading.value = true
    }

    /**
     * Marks the end of the sign-in process by resetting the loading state
     * and updating the sign-in state to unsuccessful.
     */
    fun onSignInEnd() {
        _loading.value = false
        _state.update {
            it.copy(isSignInSuccessful = false)
        }
    }

    /**
     * Updates the sign-in state and user data based on the result of the sign-in process.
     *
     * @param res The result of the sign-in operation, containing user data or an error message.
     */
    fun onSignInResult(res: SignInRes) {
        _loading.value = false

        _state.update {
            it.copy(
                isSignInSuccessful = res.data != null,
                errorMessage = res.error
            )
        }

        if (res.data != null) {
            _userData.value = res.data
        }
    }

    /**
     * Updates the current user data.
     *
     * @param userData The new user data to be set, or null to clear the user data.
     */
    fun updateAccount(userData: UserData?) {
        _userData.value = userData
    }

    /**
     * Resets the sign-in state to its initial values.
     */
    fun resetState() {
        _state.value = SignInState()
    }

    /**
     * Checks if the sign-in process was successful.
     *
     * @return True if the sign-in was successful, false otherwise.
     */
    fun isSignInSuccessful(): Boolean {
        return state.value.isSignInSuccessful
    }

    /**
     * Checks if a user is already signed in using the provided GoogleAuthClient.
     * Updates the user data and sign-in state accordingly.
     *
     * @param authClient The GoogleAuthClient used to retrieve the signed-in user.
     */
    fun checkIfUserAlreadySignedIn(authClient: GoogleAuthClient) {
        val user = authClient.getSignedInUser()
        _userData.value = user
        _state.update {
            it.copy(isSignInSuccessful = user != null)
        }
    }
}