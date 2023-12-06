package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.knowledge.testapp.data.GameState
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.viewmodels.WikiParseViewModel
import com.knowledge.testapp.ui.GameScreen
import com.knowledge.testapp.utils.LocalLanguage
import com.knowledge.testapp.utils.UserManager


class GameActivity : AppCompatActivity() {
    private val wikiParseViewModel: WikiParseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val gameState: GameState = intent.getParcelableExtra(ConstantValues.GAME_STATE)!!

            val currentTitle = remember { mutableStateOf(gameState.startConcept) }
            val goalTitle = remember { mutableStateOf(gameState.goalConcept) }
            val pathList = remember { mutableStateOf(listOf(gameState.startConcept)) }
            val totalSteps = remember { mutableStateOf(gameState.totalSteps) }
            val win = remember { mutableStateOf(gameState.win) }

            fun endQuest(win: Boolean, totalSteps: Int, pathList: List<String>) {
                gameState.win = win
                gameState.totalSteps = totalSteps
                gameState.pathList = pathList
                val intent = Intent(this@GameActivity, ResultActivity::class.java)
                intent.putExtra(ConstantValues.GAME_STATE, gameState)
                startActivity(intent)
                finish()
            }

            val languageCode = UserManager.getUser().language.languageCode
            CompositionLocalProvider(LocalLanguage provides languageCode) {
                GameScreen(
                    currentTitle = currentTitle,
                    goalTitle = goalTitle,
                    wikiParseViewModel = wikiParseViewModel,
                    pathList = pathList,
                    totalSteps = totalSteps,
                    win = win,
                    onEndQuest = { win, totalSteps, pathList ->
                        endQuest(win, totalSteps, pathList)
                    }
                )
            }
        }
    }
}