package com.training.companion.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.training.companion.R
import com.training.companion.presentation.fragments.AuthFragmentCallback

class LoginActivity : AppCompatActivity(), AuthFragmentCallback {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            startMainActivity()
        }

        val signInOptions = GoogleSignInOptions.Builder()
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, signInOptions)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    if (task.isSuccessful) {
                        val account = task.getResult(ApiException::class.java)!!
                        saveUsernameToPreferences(account.displayName!!)
                        firebaseAuthWithGoogle(account.idToken!!)
                    }
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startMainActivity()
                }
            }
    }

    private fun startGoogleAuthActivity() {
        activityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun startMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }

    private fun saveUsernameToPreferences(username: String) {
        val sharedPreferences = getSharedPreferences(PROFILE_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USERNAME, username)
        editor.apply()
    }

    override fun onAuthButtonClick() {
        startGoogleAuthActivity()
    }

    companion object {
        const val PROFILE_PREFERENCES = "profile-prefs"
        const val USERNAME = "username"
    }
}
