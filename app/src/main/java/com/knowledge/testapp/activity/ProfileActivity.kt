package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.UserViewModel
import com.knowledge.testapp.WikiHelper
import com.knowledge.testapp.databinding.ActivityProfileActivtyBinding
import com.knowledge.testapp.fragment.PathsDialogFragment
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils

class ProfileActivity : AppCompatActivity() {


    private lateinit var wikiHelper: WikiHelper
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var binding: ActivityProfileActivtyBinding
    val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
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

    fun goToMainMenu(view: View) {
        NavigationUtils.goToMainMenu(this)
    }

    fun logoutUser() {
        val googleSignInClient = userViewModel.getGoogleSignInClient(this)

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Sign out from Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Redirect to the LoginActivity after successful logout
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Handle sign out failure, possibly due to network issues
                Toast.makeText(this, "Failed to sign out from Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteAccount(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Delete user data from the database
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")
            val sanitizedEmail = currentUser.email?.let { ModifyingStrings.sanitizeEmail(it) }
            val userRecordsRef = sanitizedEmail?.let { usersRef.child(it) }

            userRecordsRef?.removeValue()?.addOnCompleteListener {
                // Delete the user from Firebase Authentication
                currentUser.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account deleted!", Toast.LENGTH_SHORT).show()

                        logoutUser()
                    } else {
                        // Handle the case where account deletion failed
                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Handle the case when the user is not available to delete
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show()
        }
    }
}