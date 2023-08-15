package com.knowledge.testapp.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.databinding.ActivityRankingsBinding
import com.knowledge.testapp.fragment.TopUsersDialogFragment
import com.knowledge.testapp.fragment.UserWorldRecordsDialogFragment
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils


class RankingsActivity : AppCompatActivity() {
    private val currentUserSanitizedEmail = ModifyingStrings.sanitizeEmail(QuizValues.USER!!.email)
    private val currentUserLanguage = QuizValues.USER!!.languageCode
    private lateinit var binding: ActivityRankingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun showTopUsersDialog_FIND_YOUR_LIKINGS(view: View) {
        val tableName = ModifyingStrings.createTableName(
            QuizValues.topUsers_FIND_YOUR_LIKINGS,
            currentUserLanguage
        )
        val dialogFragment = TopUsersDialogFragment(tableName, R.drawable.findyourlikings,
            GameMode.FIND_YOUR_LIKINGS.toString().replace("_"," "))
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserTopUsersDialog_LIKING_SPECTRUM_JOURNEY(view: View) {
        val tableName = ModifyingStrings.createTableName(
            QuizValues.topUsers_LIKING_SPECTRUM_JOURNEY,
            currentUserLanguage
        )
        val dialogFragment = TopUsersDialogFragment(tableName, R.drawable.likingspecturmjourney2,
            GameMode.LIKING_SPECTRUM_JOURNEY.toString().replace("_"," "))
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserTopUsersDialog_ANYFIN_CAN_HAPPEN(view: View) {
        val tableName = ModifyingStrings.createTableName(
            QuizValues.topUsers_ANYFIN_CAN_HAPPEN,
            currentUserLanguage
        )
        val dialogFragment = TopUsersDialogFragment(tableName, R.drawable.anyfin_can_happen,
            GameMode.ANYFIN_CAN_HAPPEN.toString().replace("_"," "))
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showUserWorldRecordsDialog_FIND_YOUR_LIKINGS(view: View) {
        val tableName = ModifyingStrings.createTableName(
            QuizValues.worldRecords_FIND_YOUR_LIKINGS,
            currentUserLanguage
        )
        val dialogFragment = UserWorldRecordsDialogFragment(currentUserSanitizedEmail, tableName, R.drawable.findyourlikings,
            GameMode.FIND_YOUR_LIKINGS.toString().replace("_"," "))
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun showUserWorldRecordsDialog_LIKING_SPECTRUM_JOURNEY(view: View) {
        val tableName = ModifyingStrings.createTableName(
            QuizValues.worldRecords_LIKING_SPECTRUM_JOURNEY,
            currentUserLanguage
        )
        val dialogFragment = UserWorldRecordsDialogFragment(currentUserSanitizedEmail, tableName, R.drawable.likingspecturmjourney2,
            GameMode.LIKING_SPECTRUM_JOURNEY.toString().replace("_"," "))
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun showUserWorldRecordsDialog_ANYFIN_CAN_HAPPEN(view: View) {
        val tableName = ModifyingStrings.createTableName(
            QuizValues.worldRecords_ANYFIN_CAN_HAPPEN,
            currentUserLanguage
        )
        val dialogFragment = UserWorldRecordsDialogFragment(currentUserSanitizedEmail, tableName, R.drawable.anyfin_can_happen,
            GameMode.ANYFIN_CAN_HAPPEN.toString().replace("_"," "))
        dialogFragment.show(supportFragmentManager, "UserWorldRecordsDialogFragment")
    }

    fun goToProfile(view: View) {
        NavigationUtils.goToProfile(this)
    }

    fun goToMainMenu(view: View) {
        NavigationUtils.goToMainMenu(this)
    }
}