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
import com.knowledge.testapp.UserViewModel
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
            Toast.makeText(this, "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val email = firebaseUser.email ?: ""

                    userViewModel.checkUserData(email) { userExists ->
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

                        userViewModel.seUserAndLanguage(this@LoginActivity)

                        val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show()
                }
            } else {
                // Display a generic error message or log the error
                Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_LONG).show()
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
                    userViewModel.seUserAndLanguage(this)
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // User not found
                            Toast.makeText(this, "User not found. Please register.", Toast.LENGTH_LONG).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid email or password
                            Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            // Other errors
                            Toast.makeText(this, "Login failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }
}