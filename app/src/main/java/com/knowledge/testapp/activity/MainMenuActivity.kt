package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameMode

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
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
}