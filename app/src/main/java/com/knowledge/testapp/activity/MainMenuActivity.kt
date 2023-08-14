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
import com.knowledge.testapp.utils.LocaleHelper
import com.knowledge.testapp.utils.ModyfingStrings

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
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