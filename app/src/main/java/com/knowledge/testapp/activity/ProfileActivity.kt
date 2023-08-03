package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.R
import com.knowledge.testapp.WikiHelper
import com.knowledge.testapp.fragment.PathsDialogFragment
import com.knowledge.testapp.utils.ModyfingStrings

class ProfileActivity : AppCompatActivity() {


    private lateinit var wikiHelper: WikiHelper
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_activty)

        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        wikiHelper = WikiHelper(this)
    }

    fun clearPaths(view: View) {
        wikiHelper.clearTableUser()
    }

    fun showWinningPathsDialog(view: View) {
        val winningPathsData = wikiHelper.getSuccessfulPaths() // Replace this with your actual logic to retrieve winning paths data
        val dialogFragment = PathsDialogFragment(winningPathsData, true)
        dialogFragment.show(supportFragmentManager, "PathsDialogFragmentTag")
    }

    fun showLosingPathsDialog(view: View) {
        val losingPathsData = wikiHelper.getUnsuccessfulPaths() // Replace this with your actual logic to retrieve winning paths data
        val dialogFragment = PathsDialogFragment(losingPathsData, false)
        dialogFragment.show(supportFragmentManager, "PathsDialogFragmentTag")
    }

    fun goToRankings(view: View) {
        startActivity(Intent(this, RankingsActivity::class.java))
    }

    fun logoutUser(view: View) {
        FirebaseAuth.getInstance().signOut()
        // Redirect to the LoginActivity after logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun deleteAccount(view: View) {

        if (currentUser != null) {

            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")
            val sanitizedEmail = currentUser.email?.let { ModyfingStrings.sanitizeEmail(it) }
            val userRecordsRef = sanitizedEmail?.let { usersRef.child(it) }
            if (userRecordsRef != null) {
                userRecordsRef.removeValue()
            }
            currentUser.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account deleted!", Toast.LENGTH_SHORT).show()
                        // Account deleted successfully
                        // You can add any additional actions here, if needed
                    } else {
                        // Failed to delete account
                        // You can handle the error here and show an error message to the user
                    }
                }
        } else {
            // User is not signed in or already signed out
            // Handle the case when the user is not available to delete
        }
    }
}