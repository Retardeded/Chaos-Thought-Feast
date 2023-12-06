package com.knowledge.testapp.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.*
import com.knowledge.testapp.R
import com.knowledge.testapp.data.User
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.UserManager
import java.sql.DriverManager
import java.util.*

class UserViewModel : ViewModel() {

    private var googleSignInClient: GoogleSignInClient? = null
    private val firebaseDatabaseRef = FirebaseDatabase.getInstance()

    companion object {
        const val KEY_USERS = "users"
    }

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
        val usersRef: DatabaseReference = firebaseDatabaseRef.getReference(KEY_USERS)
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

    fun seUserAndLanguage(context: Context, user:User) {
            UserManager.setUser(user)
            setAppLocale(context, user.language.languageCode)
    }

    fun checkIfUserExists(email: String, callback: (Boolean) -> Unit) {
        val databaseReference = firebaseDatabaseRef.getReference(KEY_USERS)
        val sanitizedEmail = ModifyingStrings.sanitizeEmail(email)

        databaseReference.child(sanitizedEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("UserViewModel", "Database error: ${databaseError.message}")
            }
        })
    }

    fun getUserDataByEmail(userEmail: String, onDataLoaded: (User?) -> Unit) {
        val usersRef: DatabaseReference = firebaseDatabaseRef.getReference(KEY_USERS)
        val userRecordsRef = usersRef.child(ModifyingStrings.sanitizeEmail(userEmail))

        userRecordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    onDataLoaded(user)
                } else {
                    onDataLoaded(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onDataLoaded(null)
            }
        })
    }

    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}