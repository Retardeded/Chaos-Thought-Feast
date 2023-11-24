package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.ui.MainMenuScreen
import com.knowledge.testapp.utils.NavigationUtils

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainMenuScreen(
                onFindLikingsClicked = { goToFindYourLikings() },
                onLikingSpectrumJourneyClicked = { goToLikingSpectrumJourney() },
                onAnyfinCanHappenClicked = { goToAnyfinCanHappen() },
                onProfileClicked = { goToProfileActivity() },
                onRankingsClicked = { goToRankingsActivity() }
            )
        }
    }

    private fun goToRankingsActivity() {
        NavigationUtils.goToRankings(this)
    }

    private fun goToProfileActivity() {
        NavigationUtils.goToProfile(this)
    }

    private fun goToFindYourLikings() {
        val intent = Intent(this, MainGameSetupActivity::class.java)
        QuizValues.gameMode = GameMode.FIND_YOUR_LIKINGS
        startActivity(intent)
    }

    private fun goToLikingSpectrumJourney() {
        val intent = Intent(this, MainGameSetupActivity::class.java)
        QuizValues.gameMode = GameMode.LIKING_SPECTRUM_JOURNEY
        startActivity(intent)
    }

    private fun goToAnyfinCanHappen() {
        val intent = Intent(this, MainGameSetupActivity::class.java)
        QuizValues.gameMode = GameMode.ANYFIN_CAN_HAPPEN
        startActivity(intent)
    }
}