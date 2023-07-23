package com.knowledge.testapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in
        if (isLoggedIn()) {
            // User is logged in, start MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not logged in, start LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Close the SplashActivity to prevent the user from going back to it
        finish()
    }

    private fun isLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
        return false
    }
}