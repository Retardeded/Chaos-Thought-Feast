package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.fragment.TopUsersDialogFragment
import com.knowledge.testapp.fragment.UserWorldRecordsDialogFragment
import com.knowledge.testapp.utils.ModyfingStrings
import com.knowledge.testapp.utils.NavigationUtils


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

    fun showUserWorldRecordsDialog_FIND_YOUR_LIKINGS(view: View) {
        val userEmail = currentUser!!.email
        val sanitizedEmail = ModyfingStrings.sanitizeEmail(userEmail!!)

        val dialogFragment = UserWorldRecordsDialogFragment(sanitizedEmail, QuizValues.tableName_FIND_YOUR_LIKINGS)
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun showUserWorldRecordsDialog_LIKING_SPECTRUM_JOURNEY(view: View) {
        val userEmail = currentUser!!.email
        val sanitizedEmail = ModyfingStrings.sanitizeEmail(userEmail!!)

        val dialogFragment = UserWorldRecordsDialogFragment(sanitizedEmail, QuizValues.tableName_LIKING_SPECTRUM_JOURNEY)
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun goToProfile(view: View) {
        NavigationUtils.goToProfile(this)
    }

    fun goToMainMenu(view: View) {
        NavigationUtils.goToMainMenu(this)
    }
}