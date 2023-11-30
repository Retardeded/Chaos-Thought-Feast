package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.viewmodels.UserViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        decideActivityToStart()
        // Close the SplashActivity to prevent the user from going back to it
        finish()
    }

    private fun decideActivityToStart() {
        // Check if the user is already logged in
        if (isLoggedIn()) {
            // User is logged in, start MainActivity
            FirebaseAuth.getInstance().currentUser?.email?.let {
                userViewModel.getUserDataByEmail(it) { user ->
                    // This code block will be executed when the data is loaded
                    // Update QuizValues.USER with the loaded user data
                    QuizValues.USER = user
                    userViewModel.setAppLocale(this, QuizValues.USER!!.language.languageCode)
                    startActivity(Intent(this, MainMenuActivity::class.java))
                }
            }
        } else {
            // User is not logged in, start LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun isLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }
}