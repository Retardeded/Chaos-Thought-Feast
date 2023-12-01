package com.knowledge.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.GameState
import com.knowledge.testapp.ui.MainMenuScreen
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.utils.NavigationUtils

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainMenuScreen(
                onFindLikingsClicked = { updateGameModeAndNavigate(GameMode.FIND_YOUR_LIKINGS) },
                onLikingSpectrumJourneyClicked = { updateGameModeAndNavigate(GameMode.LIKING_SPECTRUM_JOURNEY) },
                onAnyfinCanHappenClicked = { updateGameModeAndNavigate(GameMode.ANYFIN_CAN_HAPPEN) },
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

    private fun updateGameModeAndNavigate(gameMode: GameMode) {
        val gameState = GameState(gameMode = gameMode)
        val intent = Intent(this, MainGameSetupActivity::class.java).apply {
            putExtra(ConstantValues.GAME_STATE, gameState)
        }
        startActivity(intent)
    }
}