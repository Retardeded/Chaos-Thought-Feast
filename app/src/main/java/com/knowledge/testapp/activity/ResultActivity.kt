package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.knowledge.testapp.PathItem
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WikiHelper
import com.knowledge.testapp.data.User
import com.knowledge.testapp.utils.ModyfingStrings.Companion.sanitizeEmail

class ResultActivity : AppCompatActivity() {

    private lateinit var scoreView: TextView
    private lateinit var pathView: TextView

    private lateinit var buttonFinish: Button
    private lateinit var buttonPreviousAttempts: Button

    private var dbWikiHelper: WikiHelper? = null
    lateinit var startTitle:String
    lateinit var goalTitle:String
    var pathLength: Int = 0
    var pathList: List<String> = ArrayList()
    var win:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pathList = intent.getStringArrayListExtra(QuizValues.MOVES)!!
        pathLength = pathList.size ?: 0
        val pathText = pathList.joinToString("->")

        win = intent.getBooleanExtra(QuizValues.WIN, false)
        val totalMoves: Int = intent.getIntExtra(QuizValues.TOTAL_MOVES, 0)

            if(win)
                setContentView(R.layout.activity_result)
            else
                setContentView(R.layout.activity_lose)


        scoreView = findViewById(R.id.tv_score)
        pathView = findViewById(R.id.tv_path)
        buttonFinish = findViewById(R.id.btn_finish)
        buttonPreviousAttempts = findViewById(R.id.btn_attempts)

        startTitle = intent.getStringExtra(QuizValues.TITLE_START).toString()
        goalTitle = intent.getStringExtra(QuizValues.TITLE_GOAL).toString()

        pathView.text = pathText

            if(win)
                scoreView.text = "You found $goalTitle in $totalMoves moves"
            else
                scoreView.text = "You haven't found $goalTitle"

        saveToLocalDB()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            // Call the function to fetch the user's data and handle the result in the callback
            getUserDataByEmail(userEmail) { user ->
                if (user != null) {
                    // Example: Display the user's recordsHeld and currentScore
                    println("Records Held: ${user.recordsHeld}")
                    println("Current Score: ${user.currentScore}")

                    // Now you can pass the 'user' object to the saveWorldRecordToRemoteDB function
                    saveWorldRecordToRemoteDB(
                        user,
                        startTitle, goalTitle, pathList, pathLength, win
                    )
                } else {
                    // User's data doesn't exist in the database (not registered?)
                    println("User data not found.")
                }
            }
        }

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        buttonPreviousAttempts.setOnClickListener{
            startActivity(Intent(this, ResultsActivity::class.java))
            finish()
        }

    }

    fun getUserDataByEmail(userEmail: String, onDataLoaded: (User?) -> Unit) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        // Construct the DatabaseReference using the user's email
        val userRecordsRef = usersRef.child(sanitizeEmail(userEmail))

        // Use a ValueEventListener to fetch the user's data from the "users" table
        userRecordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the user's data exists in the database
                if (dataSnapshot.exists()) {
                    // Convert the dataSnapshot to a User object
                    val user = dataSnapshot.getValue(User::class.java)

                    // Now you have the User object containing the user's data
                    // Call the callback function and pass the User object
                    onDataLoaded(user)
                } else {
                    // User's data doesn't exist in the database (not registered?)
                    // Call the callback function with null
                    onDataLoaded(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that occurred while reading data
                // Call the callback function with null
                onDataLoaded(null)
            }
        })
    }

    fun saveWorldRecordToRemoteDB(
        user: User, // User object containing user email (or ID)
        startingConcept: String,
        goalConcept: String,
        path: List<String>,
        steps: Int,
        win: Boolean
    ) {
        if (!win)
            return

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ref: DatabaseReference = database.getReference("worldRecords")
        val usersRef: DatabaseReference = database.getReference("users")

        // Check if there is any existing record with the same startingConcept and goalConcept and win=true
        val query = ref.orderByChild("startingConcept").equalTo(startingConcept)

        val context = this

        // Use a single listener to combine the results of both queries
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var shouldSave = true

                // Loop through the existing records with the same startingConcept and goalConcept
                for (ds in dataSnapshot.children) {
                    val existingGoalConcept = ds.child("goalConcept").getValue(String::class.java)
                    val existingSteps = ds.child("steps").getValue(Int::class.java)

                    if (existingGoalConcept == goalConcept) {

                        if (existingSteps != null && existingSteps <= steps) {
                            shouldSave = false
                            break
                        } else {
                            val recordRef = ds.ref
                            val recordId = ds.key
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
                        }
                    }
                }

                // If there is no existing record or the new path length is greater, save the data
                if (shouldSave) {
                    // Generate a unique ID for the record with the user's sanitized email
                    val recordId = "${sanitizeEmail(user.email)}_${ref.push().key}"

                    val recordPath = "worldRecords/$recordId"

                    val recordData = mapOf(
                        "startingConcept" to startingConcept,
                        "goalConcept" to goalConcept,
                        "path" to path,
                        "steps" to steps,
                        "win" to win
                    )

                    FirebaseDatabase.getInstance().getReference(recordPath)
                        .setValue(recordData)
                        .addOnSuccessListener {
                            // Data successfully written to the "worldRecords" node
                            Toast.makeText(context, "New World Record!", Toast.LENGTH_SHORT).show()

                            // Update user's records and current score
                            val userRecordsRef = usersRef.child(sanitizeEmail(user.email))
                            userRecordsRef.runTransaction(object : Transaction.Handler {
                                override fun doTransaction(currentData: MutableData): Transaction.Result {
                                    val currentUser = currentData.getValue(User::class.java)

                                    if (currentUser == null) {
                                        // User doesn't exist in the database, create a new entry
                                        currentData.value = user
                                    } else {
                                        // Update the user's records and current score
                                        val newRecordsHeld = currentUser.recordsHeld + 1
                                        val newCurrentScore = currentUser.currentScore + 100

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
                        }
                        .addOnFailureListener { error ->
                            println("Error saving data: $error")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error reading data: $error")
            }
        }

        // Add the listener to the query
        query.addListenerForSingleValueEvent(listener)
    }


    fun saveToLocalDB()
    {
        dbWikiHelper = WikiHelper(this)
        dbWikiHelper!!.open()
        dbWikiHelper!!.beginTransaction()
        val itemUser = PathItem()
        itemUser.titleStart = startTitle
        itemUser.titleGoal = goalTitle
        itemUser.path = pathList.joinToString("->")
        itemUser.pathLength = pathLength
        itemUser.success = if (win) 1 else -1
        dbWikiHelper!!.insert(itemUser)
        dbWikiHelper!!.setTransactionSuccess()
        dbWikiHelper!!.endTransaction()
        dbWikiHelper!!.close()
    }
}