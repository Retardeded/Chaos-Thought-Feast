package com.knowledge.testapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.knowledge.testapp.data.User
import com.knowledge.testapp.utils.LocaleHelper
import com.knowledge.testapp.utils.ModifyingStrings
import java.sql.DriverManager

class UserViewModel : ViewModel() {
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
            LocaleHelper.getUserDataByEmail(it) { user ->
                // This code block will be executed when the data is loaded
                // Update QuizValues.USER with the loaded user data
                QuizValues.USER = user
                LocaleHelper.setAppLocale(context, user!!.languageCode)
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
}