package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.R
import com.knowledge.testapp.WikiHelper
import com.knowledge.testapp.fragment.TopUsersDialogFragment
import com.knowledge.testapp.fragment.PathsDialogFragment
import com.knowledge.testapp.fragment.UserWorldRecordsDialogFragment
import com.knowledge.testapp.utils.ModyfingStrings
import com.knowledge.testapp.utils.ModyfingStrings.Companion.sanitizeEmail


class ResultsActivity : AppCompatActivity() {
    private lateinit var wikiHelper: WikiHelper
    private lateinit var buttonFinish: Button
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        buttonFinish = findViewById(R.id.btn_try_again)
        setupRecyclerViews()

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerViews() {
        wikiHelper = WikiHelper(this)
    }

    fun clearPaths(view: View) {
        wikiHelper.clearTableUser()
    }

    fun showTopUsersDialog(view: View) {
        val dialogFragment = TopUsersDialogFragment()
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserWorldRecordsDialog(view: View) {
        val userEmail = currentUser!!.email
        val sanitizedEmail = ModyfingStrings.sanitizeEmail(userEmail!!)

        val dialogFragment = UserWorldRecordsDialogFragment(sanitizedEmail)
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
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
            val sanitizedEmail = currentUser.email?.let { sanitizeEmail(it) }
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