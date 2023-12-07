package com.knowledge.testapp.activity

import android.content.Intent
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.knowledge.testapp.viewmodels.UserViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        if (isNetworkAvailable()) {
            decideActivityToStart()
        } else {
            Toast.makeText(this, "Please turn on internet to continue", Toast.LENGTH_LONG).show()
        }

        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
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