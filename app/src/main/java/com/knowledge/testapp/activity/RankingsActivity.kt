package com.knowledge.testapp.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.*
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.PathRecord
import com.knowledge.testapp.data.User
import com.knowledge.testapp.ui.RankingsScreen
import com.knowledge.testapp.ui.TopUsersDialog
import com.knowledge.testapp.ui.UserRecordsDialog
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils


class RankingsActivity : AppCompatActivity() {
    private val currentUserSanitizedEmail = ModifyingStrings.sanitizeEmail(QuizValues.USER!!.email)
    private val currentUserLanguage = QuizValues.USER!!.language.languageCode

    private var topUsers by mutableStateOf(listOf<User>())
    private var showTopUsersFindYourLikings by mutableStateOf(false)
    private var showTopUsersLikingSpectrumJourney by mutableStateOf(false)
    private var showTopUsersAnyfinCanHappen by mutableStateOf(false)

    private var userRecords by mutableStateOf(listOf<PathRecord>())
    private var userRecordsFindYourLikings by mutableStateOf(false)
    private var userRecordsLikingSpectrumJourney by mutableStateOf(false)
    private var userRecordsAnyfinCanHappen by mutableStateOf(false)

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
                    title = QuizValues.USER!!.username,
                    gameMode = (R.string.find_your_likings),
                    userRecords = userRecords,
                    onDismissRequest = { userRecordsFindYourLikings = false }
                )
            }

            if (userRecordsLikingSpectrumJourney) {
                UserRecordsDialog(
                    backgroundResource = R.drawable.likingspecturmjourney,
                    title = QuizValues.USER!!.username,
                    gameMode = (R.string.liking_spectrum_journey),
                    userRecords = userRecords,
                    onDismissRequest = { userRecordsLikingSpectrumJourney = false }
                )
            }

            if (userRecordsAnyfinCanHappen) {
                UserRecordsDialog(
                    backgroundResource = R.drawable.anyfin_can_happen,
                    title = QuizValues.USER!!.username,
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

    private fun <T> fetchFromDatabase(
        tableName: String,
        queryModifier: Query.() -> Query = { this }, // Optional lambda to modify the query
        processSnapshot: (DataSnapshot) -> List<T>,
        onUpdate: (List<T>) -> Unit
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(tableName)
        val query = databaseReference.queryModifier()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = processSnapshot(snapshot)
                onUpdate(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    fun fetchTopUsers(tableName: String) {
        fetchFromDatabase<User>(
            tableName = tableName,
            queryModifier = { orderByChild("currentScore").limitToLast(10) },
            processSnapshot = { snapshot ->
                snapshot.children.mapNotNull { it.getValue(User::class.java) }
                    .sortedByDescending { it.currentScore }
            },
            onUpdate = { topUsers = it }
        )
    }

    fun fetchUserRecords(tableName: String, userSanitizedEmail: String) {
        fetchFromDatabase<PathRecord>(
            tableName = tableName,
            queryModifier = { orderByKey().startAt(userSanitizedEmail).endAt(userSanitizedEmail + "\uf8ff") },
            processSnapshot = { snapshot ->
                snapshot.children.mapNotNull { dataSnapshot ->
                    val startingConcept = dataSnapshot.child("startingConcept").getValue(String::class.java)
                    val goalConcept = dataSnapshot.child("goalConcept").getValue(String::class.java)
                    val steps = dataSnapshot.child("steps").getValue(Int::class.java)
                    val win = dataSnapshot.child("win").getValue(Boolean::class.java)
                    val pathList = dataSnapshot.child("path").getValue(object : GenericTypeIndicator<ArrayList<String>>() {})

                    PathRecord(startingConcept ?: "", goalConcept ?: "", pathList ?: ArrayList(), steps ?: 0, win ?: false)
                }
            },
            onUpdate = { userRecords = it }
        )
    }

    fun showDialog(
        userSanitizedEmail: String?,
        gameMode: GameMode,
        determineTableName: (GameMode) -> String,
        updateUIState: (GameMode) -> Unit
    ) {
        val tableName = determineTableName(gameMode)

        if (userSanitizedEmail == null) {
            fetchTopUsers(tableName)
        } else {
            fetchUserRecords(tableName, userSanitizedEmail)
        }

        runOnUiThread {
            updateUIState(gameMode)
        }
    }

    fun showTopUsersDialog(gameMode: GameMode) {
        showDialog(
            null,
            gameMode = gameMode,
            determineTableName = {
                ModifyingStrings.createTableName(
                    when (it) {
                        GameMode.FIND_YOUR_LIKINGS -> QuizValues.topUsers_FIND_YOUR_LIKINGS
                        GameMode.LIKING_SPECTRUM_JOURNEY -> QuizValues.topUsers_LIKING_SPECTRUM_JOURNEY
                        GameMode.ANYFIN_CAN_HAPPEN -> QuizValues.topUsers_ANYFIN_CAN_HAPPEN
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
                        GameMode.FIND_YOUR_LIKINGS -> QuizValues.worldRecords_FIND_YOUR_LIKINGS
                        GameMode.LIKING_SPECTRUM_JOURNEY -> QuizValues.worldRecords_LIKING_SPECTRUM_JOURNEY
                        GameMode.ANYFIN_CAN_HAPPEN -> QuizValues.worldRecords_ANYFIN_CAN_HAPPEN
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