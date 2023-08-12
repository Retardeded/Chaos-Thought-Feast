package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.User
import com.knowledge.testapp.databinding.ActivityMainGameSetupBinding
import com.knowledge.testapp.databinding.ActivityMainMenuBinding
import com.knowledge.testapp.utils.ModyfingStrings

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        QuizValues.USER = null
        FirebaseAuth.getInstance().currentUser?.email?.let {
            getUserDataByEmail(it) { user ->
                // This code block will be executed when the data is loaded
                // Update QuizValues.USER with the loaded user data
                QuizValues.USER = user
            }
        }
    }

    fun getUserDataByEmail(userEmail: String, onDataLoaded: (User?) -> Unit) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        // Construct the DatabaseReference using the user's email
        val userRecordsRef = usersRef.child(ModyfingStrings.sanitizeEmail(userEmail))

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

    fun goToRankingsActivity(view: View) {
        val intent = Intent(this, RankingsActivity::class.java)
        startActivity(intent)
    }

    fun goToProfileActivity(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun goToFindYourLikings(view: View) {
        val intent = Intent(this, MainGameSetupActivity::class.java)
        QuizValues.gameMode = GameMode.FIND_YOUR_LIKINGS
        startActivity(intent)
    }

    fun goToLikingSpectrumJourney(view: View) {
        val intent = Intent(this, MainGameSetupActivity::class.java)
        QuizValues.gameMode = GameMode.LIKING_SPECTRUM_JOURNEY
        startActivity(intent)
    }

    fun goToAnyfinCanHappen(view: View) {
        val intent = Intent(this, MainGameSetupActivity::class.java)
        QuizValues.gameMode = GameMode.ANYFIN_CAN_HAPPEN
        startActivity(intent)
    }

    fun goToPvP(view: View) {
        val intent = Intent(this, QueueActivity::class.java)
        startActivity(intent)
    }
}