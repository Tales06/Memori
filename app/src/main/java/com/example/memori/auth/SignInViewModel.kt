package com.example.memori.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    fun onSignInStart() {
        _loading.value = true
    }
    fun onSignInEnd() {
        _loading.value = false
        _state.update {
            it.copy(isSignInSuccessful = false)
        }
    }


    fun onSignInResult(res: SignInRes) {
        _loading.value = false

        _state.update {
            it.copy(
                isSignInSuccessful = res.data != null,
                errorMessage = res.error
            )
        }

        if(res.data != null) {
            _userData.value = res.data
        }
    }

    fun updateAccount(userData: UserData?) {
        _userData.value = userData
    }

    fun resetState() {
        _state.value = SignInState()
    }

    fun isSignInSuccessful(): Boolean {
        return state.value.isSignInSuccessful
    }

    fun checkIfUserAlreadySignedIn(authClient: GoogleAuthClient) {
        val user = authClient.getSignedInUser()
        _userData.value = user
        _state.update {
            it.copy(isSignInSuccessful = user != null)
        }
    }

}