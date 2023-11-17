package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.data.Language
import com.knowledge.testapp.data.User
import com.knowledge.testapp.ui.RegistrationScreen
import com.knowledge.testapp.utils.LocaleHelper
import com.knowledge.testapp.utils.ModifyingStrings.Companion.sanitizeEmail
import java.sql.DriverManager.println

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(username, email, selectedLanguage, recordsHeld = 0, currentScore = 0)

                    saveUserDataToDatabase(user)

                    LocaleHelper.seUserAndLanguage(this)
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
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

        val sanitizedEmail = sanitizeEmail(user.email)
        val userRecordsRef = usersRef.child(sanitizedEmail)

        userRecordsRef.setValue(user)
            .addOnSuccessListener {
                println("User data saved successfully.")
            }
            .addOnFailureListener { error ->
                println("Error saving user data: $error")
            }
    }
}