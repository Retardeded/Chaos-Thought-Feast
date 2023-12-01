package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.viewmodels.UserViewModel
import com.knowledge.testapp.ui.ProfilePathsDialog
import com.knowledge.testapp.ui.ProfileScreen
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils
import com.knowledge.testapp.viewmodels.LocalDataViewModel

class ProfileActivity : AppCompatActivity() {

    private var showWinningPathsDialog by mutableStateOf(false)
    private var showLosingPathsDialog by mutableStateOf(false)
    private val userViewModel: UserViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localDataViewModel.fetchPathsForUser(ConstantValues.USER!!.email, true) // For winning paths
        localDataViewModel.fetchPathsForUser(ConstantValues.USER!!.email, false) // For losing paths

        setContent {
            if (showWinningPathsDialog) {
                ProfilePathsDialog(
                    true,
                    ConstantValues.USER!!.username,
                    pathsData = localDataViewModel.winningPathsData.value,
                    onDismissRequest = { showWinningPathsDialog = false }
                )
            }
            if (showLosingPathsDialog) {
                ProfilePathsDialog(
                    false,
                    ConstantValues.USER!!.username,
                    pathsData = localDataViewModel.losingPathsData.value,
                    onDismissRequest = { showLosingPathsDialog = false }
                )
            }
            ProfileScreen(
                onShowWinningPaths = { showWinningPathsDialog() },
                onShowLosingPaths = { showLosingPathsDialog() },
                onClearPaths = { clearPaths() },
                onDeleteAccount = { deleteAccount() },
                onLogout = { logoutUser() },
                onGoToMainMenu = { goToMainMenu() },
                onGoToRankings = { goToRankings() }
            )
        }
    }

    fun clearPaths() {
        localDataViewModel.clearUserData(ConstantValues.USER!!.email)
    }

    fun showWinningPathsDialog() {
        runOnUiThread { showWinningPathsDialog = true }
    }

    fun showLosingPathsDialog() {
        runOnUiThread { showLosingPathsDialog = true }
    }

    fun goToRankings() {
        NavigationUtils.goToRankings(this)
    }

    fun goToMainMenu() {
        NavigationUtils.goToMainMenu(this)
    }

    fun logoutUser() {
        val googleSignInClient = userViewModel.getGoogleSignInClient(this)

        FirebaseAuth.getInstance().signOut()

        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to sign out from Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteAccount() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val usersRef: DatabaseReference = database.getReference("users")
            val sanitizedEmail = currentUser.email?.let { ModifyingStrings.sanitizeEmail(it) }
            val userRecordsRef = sanitizedEmail?.let { usersRef.child(it) }

            userRecordsRef?.removeValue()?.addOnCompleteListener {
                currentUser.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account deleted!", Toast.LENGTH_SHORT).show()

                        logoutUser()
                    } else {
                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show()
        }
    }
}