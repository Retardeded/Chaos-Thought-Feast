package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.knowledge.testapp.R
import com.knowledge.testapp.fragment.TopUsersDialogFragment
import com.knowledge.testapp.fragment.UserWorldRecordsDialogFragment
import com.knowledge.testapp.utils.ModyfingStrings


class RankingsActivity : AppCompatActivity() {
    private lateinit var buttonFinish: Button
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rankings)
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

    fun goToProfile(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun onTryAgainButtonClick(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}