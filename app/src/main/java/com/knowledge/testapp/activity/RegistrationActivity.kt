package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.knowledge.testapp.R
import com.knowledge.testapp.viewmodels.UserViewModel
import com.knowledge.testapp.data.Language
import com.knowledge.testapp.data.User
import com.knowledge.testapp.ui.RegistrationScreen

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            RegistrationScreen(
                onRegisterClick = { username, email, password, selectedLanguage ->
                    registerUser(username, email, password, selectedLanguage)
                },
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }

    private fun registerUser(username: String, email: String, password: String, selectedLanguage : Language) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(username, email, selectedLanguage, recordsHeld = 0, currentScore = 0)
                    userViewModel.saveUserDataToDatabase(user)
                    userViewModel.seUserAndLanguage(this, user)
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }  else {
                    when (val exception = task.exception) {
                        is FirebaseAuthWeakPasswordException -> {
                            Toast.makeText(this, getString(R.string.weak_password), Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(this, getString(R.string.email_in_use), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, getString(R.string.registration_failed, exception?.message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }
}