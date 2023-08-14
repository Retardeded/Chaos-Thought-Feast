package com.knowledge.testapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.databinding.ActivityRankingsBinding
import com.knowledge.testapp.fragment.TopUsersDialogFragment
import com.knowledge.testapp.fragment.UserWorldRecordsDialogFragment
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils


class RankingsActivity : AppCompatActivity() {
    private val currentUserSanitizedEmail = ModifyingStrings.sanitizeEmail(QuizValues.USER!!.email)
    private lateinit var binding: ActivityRankingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun showTopUsersDialog_FIND_YOUR_LIKINGS(view: View) {
        val dialogFragment = TopUsersDialogFragment(QuizValues.topUsers_FIND_YOUR_LIKINGS)
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserTopUsersDialog_LIKING_SPECTRUM_JOURNEY(view: View) {
        val dialogFragment = TopUsersDialogFragment(QuizValues.topUsers_LIKING_SPECTRUM_JOURNEY)
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserTopUsersDialog_ANYFIN_CAN_HAPPEN(view: View) {
        val dialogFragment = TopUsersDialogFragment(QuizValues.topUsers_ANYFIN_CAN_HAPPEN)
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserWorldRecordsDialog_FIND_YOUR_LIKINGS(view: View) {
        val dialogFragment = UserWorldRecordsDialogFragment(currentUserSanitizedEmail, QuizValues.worldRecords_FIND_YOUR_LIKINGS)
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun showUserWorldRecordsDialog_LIKING_SPECTRUM_JOURNEY(view: View) {
        val dialogFragment = UserWorldRecordsDialogFragment(currentUserSanitizedEmail, QuizValues.worldsRecords_LIKING_SPECTRUM_JOURNEY)
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun showUserWorldRecordsDialog_ANYFIN_CAN_HAPPEN(view: View) {
        val dialogFragment = UserWorldRecordsDialogFragment(currentUserSanitizedEmail, QuizValues.worldsRecords_ANYFIN_CAN_HAPPEN)
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun goToProfile(view: View) {
        NavigationUtils.goToProfile(this)
    }

    fun goToMainMenu(view: View) {
        NavigationUtils.goToMainMenu(this)
    }
}