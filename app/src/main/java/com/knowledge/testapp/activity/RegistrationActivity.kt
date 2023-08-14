package com.knowledge.testapp.activity

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.data.Language
import com.knowledge.testapp.data.User
import com.knowledge.testapp.databinding.ActivityRegistrationBinding
import com.knowledge.testapp.utils.LocaleHelper
import com.knowledge.testapp.utils.ModifyingStrings.Companion.sanitizeEmail

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languageSpinner: Spinner = binding.languageSpinner

        val languageOptions = Language.values()
        val languageLabels = languageOptions.map { it.name }
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, languageLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val user = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val selectedLanguageLabel = binding.languageSpinner.selectedItem as String
            val selectedLanguage = Language.valueOf(selectedLanguageLabel)

            // Call the register function with email and password
            registerUser(user, email, password, selectedLanguage)
        }

        binding.tvLogin.setOnClickListener {
            // Redirect to LoginActivity when "Already have an account? Login here" is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, email: String, password: String, selectedLanguage : Language) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Create a User object with the user's email and other initial data
                    val user = User(username, email, selectedLanguage, recordsHeld = 0, currentScore = 0)

                    // Save the user data to the "users" table in the Firebase Realtime Database
                    saveUserDataToDatabase(user)

                    // Proceed to the next activity (e.g., MainActivity)
                    LocaleHelper.seUserAndLanguage(this)
                    val intent = Intent(this, MainMenuActivity::class.java)
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