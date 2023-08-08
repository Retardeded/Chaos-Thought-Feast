package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.knowledge.testapp.R

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btnLogin)
        registerButton = findViewById(R.id.btnRegister)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // Call the login function with email and password
            loginUser(email, password)
        }

        registerButton.setOnClickListener {
            // Redirect to RegistrationActivity when "Register here" button is clicked
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, user is authenticated
                    // Proceed to the next activity (e.g., MainActivity)
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed, handle the error
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        // If the user does not exist
                        // Handle accordingly, e.g., show an error message or prompt for registration
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        // If the user enters incorrect email or password
                        // Handle accordingly, e.g., show an error message
                    } catch (e: Exception) {
                        // Handle other exceptions
                    }
                }
            }
    }
}