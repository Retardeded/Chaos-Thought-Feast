package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
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
        FirebaseAuth.getInstance().currentUser?.email?.let { email ->
            userViewModel.getUserDataByEmail(email) { user ->
                user?.let {
                        userViewModel.seUserAndLanguage(this, user)
                        startActivity(Intent(this, MainMenuActivity::class.java))
                    }
            }
        } ?: run {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun isLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }
}