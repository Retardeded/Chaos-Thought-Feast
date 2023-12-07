package com.knowledge.testapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.knowledge.testapp.R
import com.knowledge.testapp.viewmodels.UserViewModel
import com.knowledge.testapp.data.Language
import com.knowledge.testapp.data.User
import com.knowledge.testapp.ui.LoginScreen
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val userViewModel: UserViewModel by viewModels()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e("LoginActivity", "Google Sign-In failed. Status Code: ${e.statusCode}", e)
            Toast.makeText(this, getString(R.string.google_sign_in_failed, e.localizedMessage), Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val email = firebaseUser.email ?: ""

                    userViewModel.checkIfUserExists(email) { userExists ->
                        if (!userExists) {

                            val locale = Locale.getDefault()
                            val language = when (locale.language) {
                                "en" -> Language.ENGLISH
                                "pl" -> Language.POLISH
                                else -> Language.ENGLISH // Default language
                            }

                            val newUser = User(
                                username = firebaseUser.displayName ?: "",
                                email = email,
                                language = language
                            )
                            userViewModel.saveUserDataToDatabase(newUser)
                        }

                        userViewModel.getUserDataByEmail(email) {
                            userViewModel.seUserAndLanguage(this@LoginActivity, it!!)
                        }

                        val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.google_sign_in_failed_generic), Toast.LENGTH_LONG).show()
                Log.e("LoginActivity", "Google Sign-In failed: ${task.exception}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val googleSignInClient = userViewModel.getGoogleSignInClient(this)

        setContent {
            LoginScreen(
                onGoogleSignInClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                onLoginClick = { email, password ->
                    loginUser(email, password)
                },
                onRegisterClick = {
                    startActivity(Intent(this, RegistrationActivity::class.java))
                }
            )
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userViewModel.getUserDataByEmail(email) {
                        userViewModel.seUserAndLanguage(this@LoginActivity, it!!)
                    }
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    when (val exception = task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(this, getString(R.string.user_not_found), Toast.LENGTH_LONG).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, getString(R.string.invalid_email_or_password), Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this, getString(R.string.login_failed, exception?.message), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }
}