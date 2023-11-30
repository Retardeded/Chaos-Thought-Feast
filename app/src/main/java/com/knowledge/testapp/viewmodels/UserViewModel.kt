package com.knowledge.testapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.data.User
import com.knowledge.testapp.utils.ModifyingStrings
import java.sql.DriverManager
import java.util.*

class UserViewModel : ViewModel() {

    private var googleSignInClient: GoogleSignInClient? = null

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        if (googleSignInClient == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)
        }
        return googleSignInClient!!
    }

    fun saveUserDataToDatabase(user: User) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        val sanitizedEmail = ModifyingStrings.sanitizeEmail(user.email)
        val userRecordsRef = usersRef.child(sanitizedEmail)

        userRecordsRef.setValue(user)
            .addOnSuccessListener {
                DriverManager.println("User data saved successfully.")
            }
            .addOnFailureListener { error ->
                DriverManager.println("Error saving user data: $error")
            }
    }

    fun seUserAndLanguage(context: Context) {
        FirebaseAuth.getInstance().currentUser?.email?.let {
            getUserDataByEmail(it) { user ->
                // This code block will be executed when the data is loaded
                // Update QuizValues.USER with the loaded user data
                QuizValues.USER = user
                setAppLocale(context, user!!.language.languageCode)
            }
        }
    }

    fun checkUserData(email: String, callback: (Boolean) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val sanitizedEmail = ModifyingStrings.sanitizeEmail(email)

        databaseReference.child(sanitizedEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
                Log.e("UserViewModel", "Database error: ${databaseError.message}")
            }
        })
    }

    fun getUserDataByEmail(userEmail: String, onDataLoaded: (User?) -> Unit) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        // Construct the DatabaseReference using the user's email
        val userRecordsRef = usersRef.child(ModifyingStrings.sanitizeEmail(userEmail))

        // Use a ValueEventListener to fetch the user's data from the "users" table
        userRecordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the user's data exists in the database
                if (dataSnapshot.exists()) {
                    // Convert the dataSnapshot to a User object
                    val user = dataSnapshot.getValue(User::class.java)

                    // Now you have the User object containing the user's data
                    // Call the callback function and pass the User object
                    onDataLoaded(user)
                } else {
                    // User's data doesn't exist in the database (not registered?)
                    // Call the callback function with null
                    onDataLoaded(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occurred while reading data
                // Call the callback function with null
                onDataLoaded(null)
            }
        })
    }

    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language) // Convert Language enum to locale
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}