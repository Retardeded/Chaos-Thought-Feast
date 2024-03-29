package com.knowledge.testapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.User
import com.knowledge.testapp.utils.ModifyingStrings.Companion.createTableName
import com.knowledge.testapp.utils.ModifyingStrings.Companion.sanitizeEmail
import com.knowledge.testapp.utils.NavigationUtils
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameState
import com.knowledge.testapp.ui.LoseScreen
import com.knowledge.testapp.ui.WinScreen
import com.knowledge.testapp.utils.UserManager
import com.knowledge.testapp.viewmodels.LocalDataViewModel
import com.knowledge.testapp.viewmodels.UserViewModel

class ResultActivity : AppCompatActivity() {
    var pathLength: Int = 0
    var diffrenceBetweenPreviousBest = 0
    private var totalSteps = 0
    var pathList:List<String> = ArrayList()
    private var additionalTextState = mutableStateOf("")
    private val userViewModel: UserViewModel by viewModels()
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private lateinit var gameState: GameState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameState= intent.getParcelableExtra(ConstantValues.GAME_STATE)!!

        val win = gameState.win
        val startConcept = gameState.startConcept
        val goalConcept = gameState.goalConcept
        pathList = gameState.pathList
        totalSteps = gameState.totalSteps
        pathLength = pathList.size ?: 0
        val pathText = pathList.joinToString("->")

        setContent {
            if (win) {
                WinScreen(
                    resultText = additionalTextState,
                    pathText = pathText,
                    onGoToMainMenu = { goToMainMenu() },
                    onTryAgain = { tryAgain() }
                )
                processTheResult(startConcept, goalConcept)
            } else {
                LoseScreen(
                    goalConcept = goalConcept,
                    pathText = pathText,
                    onGoToMainMenu = { goToMainMenu() },
                    onTryAgain = { tryAgain() }
                )
            }
        }
        savePathToDatabase(win, startConcept, goalConcept)
    }

    fun tryAgain() {
        NavigationUtils.tryAgain(this, gameState)
    }

    fun goToMainMenu() {
        NavigationUtils.goToMainMenu(this)
    }

    fun processTheResult(startConcept: String, goalConcept: String) {
        FirebaseAuth.getInstance().currentUser?.email?.let { userEmail ->
            userViewModel.getUserDataByEmail(userEmail) { user ->
                user?.let {
                    logUserDetails(user)
                    saveWorldRecordToRemoteDB(user, startConcept, goalConcept, pathList, totalSteps)
                } ?: run {
                    logUserDataNotFound()
                }
            }
        }
    }

    private fun logUserDetails(user: User) {
        println("Records Held: ${user.recordsHeld}")
        println("Current Score: ${user.currentScore}")
    }

    private fun logUserDataNotFound() {
        println("User data not found.")
    }

    fun saveWorldRecordToRemoteDB(
        user: User,
        startingConcept: String,
        goalConcept: String,
        path: List<String>,
        totalSteps: Int
    ) {
        val (refStringWorldsRecords, refStringUsers) = getDatabaseReferences(user)

        val worldRecordsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(refStringWorldsRecords)
        val topUsersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(refStringUsers)
        val query = worldRecordsRef.orderByChild("startingGoalConcept").equalTo("${startingConcept}_$goalConcept")

        query.addListenerForSingleValueEvent(createWorldRecordListener(topUsersRef, user, startingConcept, goalConcept, path, totalSteps, refStringWorldsRecords))
    }

    private fun getDatabaseReferences(user: User): Pair<String, String> {
        val gameModeTables = when (gameState.gameMode) {
            GameMode.FIND_YOUR_LIKINGS -> Pair(ConstantValues.worldRecords_FIND_YOUR_LIKINGS, ConstantValues.topUsers_FIND_YOUR_LIKINGS)
            GameMode.LIKING_SPECTRUM_JOURNEY -> Pair(ConstantValues.worldRecords_LIKING_SPECTRUM_JOURNEY, ConstantValues.topUsers_LIKING_SPECTRUM_JOURNEY)
            GameMode.ANYFIN_CAN_HAPPEN -> Pair(ConstantValues.worldRecords_ANYFIN_CAN_HAPPEN, ConstantValues.topUsers_ANYFIN_CAN_HAPPEN)
        }

        return gameModeTables.let { (worldRecords, topUsers) ->
            Pair(createTableName(worldRecords, user.language.languageCode), createTableName(topUsers, user.language.languageCode))
        }
    }

    private fun createWorldRecordListener(
        topUsersRef: DatabaseReference,
        user: User,
        startingConcept: String,
        goalConcept: String,
        path: List<String>,
        totalSteps: Int,
        refStringWorldsRecords: String
    ): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val existingRecord = dataSnapshot.children.firstOrNull()

                var brandNewPath = true

                val worldRecord = if (existingRecord != null) {
                    brandNewPath = false
                    handleExistingRecord(topUsersRef, existingRecord, totalSteps)
                } else true // If there is no existing record, assume it's a world record

                handleNewRecord(topUsersRef, brandNewPath, worldRecord, user, startingConcept, goalConcept, path, totalSteps, refStringWorldsRecords)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error reading data: $error")
            }
        }
    }

    private fun handleExistingRecord(
        usersRef: DatabaseReference,
        existingRecord: DataSnapshot,
        totalSteps: Int
    ): Boolean {
        val existingSteps = existingRecord.child("steps").getValue(Int::class.java) ?: return true
        if (existingSteps <= totalSteps) {
            return false // Not a world record
        } else {
            diffrenceBetweenPreviousBest = totalSteps-existingSteps
            val recordRef = existingRecord.ref
            val recordId = existingRecord.key
            val userSanitizedEmail = recordId!!.substringBefore("_") // Extract user email from the world record ID

            // Update the user's records and score as a record is removed
            val userRecordsRef = usersRef.child(userSanitizedEmail)
            userRecordsRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentUser = currentData.getValue(User::class.java)

                    if (currentUser != null) {
                        // Update the user's records and current score
                        val newRecordsHeld = currentUser.recordsHeld - 1
                        val newCurrentScore = currentUser.currentScore - 100

                        currentData.child("recordsHeld").value = newRecordsHeld
                        currentData.child("currentScore").value = newCurrentScore
                    }

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    // Handle transaction completion
                    if (error != null) {
                        println("Transaction failed: ${error.message}")
                    } else {
                        println("Transaction completed successfully.")
                    }
                }
            })

            // Remove the world record
            recordRef.removeValue()
            return true
        }
    }

    private fun handleNewRecord(
        usersRef: DatabaseReference,
        brandNewPath: Boolean,
        worldRecord: Boolean,
        user: User,
        startConcept: String,
        goalConcept: String,
        path: List<String>,
        totalSteps: Int,
        refStringWorldsRecords: String
    ) {
        if (worldRecord) {
            val recordId = "${sanitizeEmail(user.email)}_${startConcept}_$goalConcept"
            val recordPath = "$refStringWorldsRecords/$recordId"
            val recordData = mapOf(
                "startConcept" to startConcept,
                "goalConcept" to goalConcept,
                "path" to path,
                "steps" to totalSteps,
                "win" to true,
                "startingGoalConcept" to "${startConcept}_$goalConcept"
            )

            FirebaseDatabase.getInstance().getReference(recordPath)
                .setValue(recordData)
                .addOnSuccessListener {
                    updateWorldRecordUserScore(user, usersRef)
                    updateUI(goalConcept, brandNewPath, worldRecord, diffrenceBetweenPreviousBest, totalSteps) // Update UI accordingly
                }
                .addOnFailureListener {
                    // Handle failure
                }
        } else {
            updateUI(goalConcept, brandNewPath, worldRecord, diffrenceBetweenPreviousBest, totalSteps)
        }
    }

    private fun updateWorldRecordUserScore(user: User, usersRef: DatabaseReference) {

        val userRecordsRef = usersRef.child(sanitizeEmail(user.email))
        userRecordsRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentUser = currentData.getValue(User::class.java)
                if (currentUser == null) {
                    // User doesn't exist in the database, create a new entry
                    val newUser = user.copy(recordsHeld = 1, currentScore = 100)
                    currentData.setValue(newUser)
                } else {
                    // Update the user's records and current score
                    val newRecordsHeld = currentUser.recordsHeld + 1
                    val newCurrentScore = currentUser.currentScore + 100
                    currentData.child("recordsHeld").setValue(newRecordsHeld)
                    currentData.child("currentScore").setValue(newCurrentScore)
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    println("Transaction failed: ${error.message}")
                } else {
                    println("Transaction completed successfully.")
                }
            }
        })
    }

    private fun updateUI(goalConcept: String, brandNewPath: Boolean, worldRecord: Boolean, diff: Int, totalSteps: Int) {
        val formattedString = getString(R.string.found_concept_steps)
        val resultInformationText = formattedString.format(goalConcept, totalSteps)
        val addText = if (worldRecord) {
            if(brandNewPath) {
                val textWRomantic = getString(R.string.new_world_record_romantic)
                textWRomantic
            } else {
                val newWorldRecordWithDiff = getString(R.string.new_world_record_with_diff, -diff)
                newWorldRecordWithDiff
            }

        } else {
            val textNotEnoughForWR = getString(R.string.not_enough_for_wr)
            "$diff $textNotEnoughForWR"
        }
        additionalTextState.value = "$resultInformationText\n$addText"
        Toast.makeText(this, additionalTextState.value, Toast.LENGTH_SHORT).show()
        print(additionalTextState.value)
    }


    fun savePathToDatabase(win: Boolean, startConcept: String, goalConcept: String) {
        val userId = UserManager.getUser().email
        localDataViewModel.savePathToDatabase(userId, win, startConcept, goalConcept, pathList as ArrayList<String>, pathLength)
    }
}