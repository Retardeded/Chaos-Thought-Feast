package com.knowledge.testapp.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.*
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.PathRecord
import com.knowledge.testapp.data.User
import com.knowledge.testapp.ui.RankingsScreen
import com.knowledge.testapp.ui.TopUsersDialog
import com.knowledge.testapp.ui.UserRecordsDialog
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils
import com.knowledge.testapp.utils.UserManager
import com.knowledge.testapp.viewmodels.RankingsViewModel


class RankingsActivity : AppCompatActivity() {
    private val currentUserSanitizedEmail = ModifyingStrings.sanitizeEmail(UserManager.getUser().email)
    private val currentUserLanguage = UserManager.getUser().language.languageCode
    private val currentUserName = UserManager.getUser().username

    private var topUsers by mutableStateOf(listOf<User>())
    private var showTopUsersFindYourLikings by mutableStateOf(false)
    private var showTopUsersLikingSpectrumJourney by mutableStateOf(false)
    private var showTopUsersAnyfinCanHappen by mutableStateOf(false)

    private var userRecords by mutableStateOf(listOf<PathRecord>())
    private var userRecordsFindYourLikings by mutableStateOf(false)
    private var userRecordsLikingSpectrumJourney by mutableStateOf(false)
    private var userRecordsAnyfinCanHappen by mutableStateOf(false)
    private val rankingsViewModel: RankingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            if (showTopUsersFindYourLikings) {
                TopUsersDialog(
                    backgroundResource = R.drawable.findyourlikings,
                    title = this.getString(R.string.top_users),
                    gameMode =  (R.string.find_your_likings),
                    topUsers = topUsers,
                    onDismissRequest = { showTopUsersFindYourLikings = false }
                )
            }

            if (showTopUsersLikingSpectrumJourney) {
                TopUsersDialog(
                    backgroundResource = R.drawable.likingspecturmjourney,
                    title = this.getString(R.string.top_users),
                    gameMode = (R.string.liking_spectrum_journey),
                    topUsers = topUsers,
                    onDismissRequest = { showTopUsersLikingSpectrumJourney = false }
                )
            }

            if (showTopUsersAnyfinCanHappen) {
                TopUsersDialog(
                    backgroundResource = R.drawable.anyfin_can_happen,
                    title = this.getString(R.string.top_users),
                    gameMode = (R.string.anyfin_can_happen),
                    topUsers = topUsers,
                    onDismissRequest = { showTopUsersAnyfinCanHappen = false }
                )
            }

            if (userRecordsFindYourLikings) {
                UserRecordsDialog(
                    backgroundResource = R.drawable.findyourlikings,
                    title = currentUserName,
                    gameMode = (R.string.find_your_likings),
                    userRecords = userRecords,
                    onDismissRequest = { userRecordsFindYourLikings = false }
                )
            }

            if (userRecordsLikingSpectrumJourney) {
                UserRecordsDialog(
                    backgroundResource = R.drawable.likingspecturmjourney,
                    title = currentUserName,
                    gameMode = (R.string.liking_spectrum_journey),
                    userRecords = userRecords,
                    onDismissRequest = { userRecordsLikingSpectrumJourney = false }
                )
            }

            if (userRecordsAnyfinCanHappen) {
                UserRecordsDialog(
                    backgroundResource = R.drawable.anyfin_can_happen,
                    title = currentUserName,
                    gameMode = (R.string.anyfin_can_happen),
                    userRecords = userRecords,
                    onDismissRequest = { userRecordsAnyfinCanHappen = false }
                )
            }

            RankingsScreen(
                onFindYourLikingsClick = { showTopUsersDialog(GameMode.FIND_YOUR_LIKINGS) },
                onLikingSpectrumJourneyClick = { showTopUsersDialog(GameMode.LIKING_SPECTRUM_JOURNEY) },
                onAnyfinCanHappenClick = { showTopUsersDialog(GameMode.ANYFIN_CAN_HAPPEN) },
                onFindYourLikingsRecordsClick = { showUserWorldRecordsDialog(GameMode.FIND_YOUR_LIKINGS, currentUserSanitizedEmail) },
                onLikingSpectrumJourneyRecordsClick = { showUserWorldRecordsDialog(GameMode.LIKING_SPECTRUM_JOURNEY, currentUserSanitizedEmail) },
                onAnyfinCanHappenRecordsClick = { showUserWorldRecordsDialog(GameMode.ANYFIN_CAN_HAPPEN, currentUserSanitizedEmail) },
                onGoToMainMenuClick = { goToMainMenu() },
                onGoToProfileClick = { goToProfile() }
            )
        }
    }

    fun showDialog(
        userSanitizedEmail: String?,
        gameMode: GameMode,
        determineTableName: (GameMode) -> String,
        updateUIState: (GameMode) -> Unit
    ) {
        val tableName = determineTableName(gameMode)

        val onSuccessUsers: (List<User>) -> Unit = { users ->
            topUsers = users
            runOnUiThread {
                updateUIState(gameMode)
            }
        }

        val onSuccessRecords: (List<PathRecord>) -> Unit = { records ->
            userRecords = records
            runOnUiThread {
                updateUIState(gameMode)
            }
        }

        val onFailure: (DatabaseError) -> Unit = { error ->
            Log.e("FirebaseError", error.message ?: "Unknown error")
        }

        if (userSanitizedEmail == null) {
            rankingsViewModel.fetchTopUsers(tableName, 10, onSuccessUsers, onFailure)
        } else {
            rankingsViewModel.fetchUserRecords(tableName, userSanitizedEmail, onSuccessRecords, onFailure)
        }
    }

    fun showTopUsersDialog(gameMode: GameMode) {
        showDialog(
            null,
            gameMode = gameMode,
            determineTableName = {
                ModifyingStrings.createTableName(
                    when (it) {
                        GameMode.FIND_YOUR_LIKINGS -> ConstantValues.topUsers_FIND_YOUR_LIKINGS
                        GameMode.LIKING_SPECTRUM_JOURNEY -> ConstantValues.topUsers_LIKING_SPECTRUM_JOURNEY
                        GameMode.ANYFIN_CAN_HAPPEN -> ConstantValues.topUsers_ANYFIN_CAN_HAPPEN
                    },
                    currentUserLanguage
                )
            },
            updateUIState = {
                when (it) {
                    GameMode.FIND_YOUR_LIKINGS -> showTopUsersFindYourLikings = true
                    GameMode.LIKING_SPECTRUM_JOURNEY -> showTopUsersLikingSpectrumJourney = true
                    GameMode.ANYFIN_CAN_HAPPEN -> showTopUsersAnyfinCanHappen = true
                }
            }
        )
    }

    fun showUserWorldRecordsDialog(gameMode: GameMode, userSanitizedEmail: String) {
        showDialog(
            userSanitizedEmail,
            gameMode = gameMode,
            determineTableName = {
                ModifyingStrings.createTableName(
                    when (it) {
                        GameMode.FIND_YOUR_LIKINGS -> ConstantValues.worldRecords_FIND_YOUR_LIKINGS
                        GameMode.LIKING_SPECTRUM_JOURNEY -> ConstantValues.worldRecords_LIKING_SPECTRUM_JOURNEY
                        GameMode.ANYFIN_CAN_HAPPEN -> ConstantValues.worldRecords_ANYFIN_CAN_HAPPEN
                    },
                    currentUserLanguage
                )
            },
            updateUIState = {
                when (it) {
                    GameMode.FIND_YOUR_LIKINGS -> userRecordsFindYourLikings = true
                    GameMode.LIKING_SPECTRUM_JOURNEY -> userRecordsLikingSpectrumJourney = true
                    GameMode.ANYFIN_CAN_HAPPEN -> userRecordsAnyfinCanHappen = true
                }
            }
        )
    }

    fun goToProfile() {
        NavigationUtils.goToProfile(this)
    }

    fun goToMainMenu() {
        NavigationUtils.goToMainMenu(this)
    }
}