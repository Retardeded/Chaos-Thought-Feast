package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.R
import com.knowledge.testapp.data.User
import com.knowledge.testapp.utils.ModyfingStrings.Companion.sanitizeEmail

class RegistrationActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.btnRegister)
        loginTextView = findViewById(R.id.textViewLogin)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // Call the register function with email and password
            registerUser(email, password)
        }

        loginTextView.setOnClickListener {
            // Redirect to LoginActivity when "Already have an account? Login here" is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, user is created and authenticated

                    // Get the newly registered user's email
                    val userEmail = email

                    // Create a User object with the user's email and other initial data
                    val user = User(userEmail, recordsHeld = 0, currentScore = 0)

                    // Save the user data to the "users" table in the Firebase Realtime Database
                    saveUserDataToDatabase(user)

                    // Proceed to the next activity (e.g., MainActivity)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Registration failed, handle the error
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        // If the email is not valid
                        // Handle accordingly, e.g., show an error message
                    } catch (e: Exception) {
                        // Handle other exceptions
                    }
                }
            }
    }

    private fun saveUserDataToDatabase(user: User) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        // Sanitize the user's email and use it as the key to store their data in the "users" table
        val sanitizedEmail = sanitizeEmail(user.email)
        val userRecordsRef = usersRef.child(sanitizedEmail)

        // Save the user data to the "users" table
        userRecordsRef.setValue(user)
            .addOnSuccessListener {
                println("User data saved successfully.")
            }
            .addOnFailureListener { error ->
                println("Error saving user data: $error")
            }
    }
}