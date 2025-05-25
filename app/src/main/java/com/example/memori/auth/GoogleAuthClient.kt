package com.example.memori.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.memori.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(
    private val context: Context
) {


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Lazily initializes the [GoogleSignInClient] instance using the application's context.
     *
     * This client is configured with default sign-in options, requests an ID token using the
     * web client ID from resources, and requests the user's email address.
     *
     * @see GoogleSignInOptions
     * @see GoogleSignInClient
     */
    /**
     * Client GoogleSignIn utilizzato per gestire l'autenticazione tramite Google.
     *
     * Viene inizializzato in modo lazy con le opzioni di accesso predefinite,
     * richiedendo l'ID token e l'email dell'utente.
     * L'ID client web viene recuperato dalle risorse tramite il contesto.
     *
     * @see GoogleSignInClient
     * @see GoogleSignInOptions
     */
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    /**
     * Returns an [Intent] that can be used to start the Google Sign-In flow.
     *
     * @return the sign-in [Intent] from the [googleSignInClient].
     */
    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    /**
     * Attempts to sign in a user using the provided Google Sign-In intent data.
     *
     * This function retrieves the Google account from the intent, obtains the authentication
     * credential, and signs in with Firebase Authentication. If successful, it returns a [SignInRes]
     * containing the authenticated user's data. If an error occurs during the process, it returns
     * a [SignInRes] with the error message.
     *
     * @param data The [Intent] returned from the Google Sign-In activity.
     * @return [SignInRes] containing either the signed-in user's data or an error message.
     */
    /**
     * Gestisce il processo di accesso tramite Google utilizzando un Intent.
     *
     * @param data L'Intent ricevuto dal risultato dell'attività di accesso di Google.
     * @return Un oggetto [SignInRes] che contiene i dati dell'utente autenticato oppure un messaggio di errore in caso di fallimento.
     *
     * Questo metodo tenta di ottenere l'account Google dall'Intent, crea una credenziale di autenticazione,
     * e accede tramite Firebase Authentication. In caso di successo, restituisce i dati dell'utente;
     * in caso di errore, restituisce il messaggio di errore.
     */
    suspend fun signInWithIntent(data: Intent): SignInRes {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            val user = auth.signInWithCredential(credential).await().user
            SignInRes(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePicture = photoUrl?.toString()
                    )
                },
                error = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInRes(
                data = null,
                error = e.message
            )
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
        auth.signOut()
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePicture = photoUrl?.toString()
        )
    }
}
