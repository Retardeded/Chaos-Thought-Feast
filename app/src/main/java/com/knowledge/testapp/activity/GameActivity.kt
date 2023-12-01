package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.knowledge.testapp.utils.ConstantValues
import com.knowledge.testapp.viewmodels.WikiParseViewModel
import com.knowledge.testapp.ui.GameScreen


class GameActivity : AppCompatActivity() {
    private lateinit var wikiParseViewModel: WikiParseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        wikiParseViewModel = WikiParseViewModel()

        setContent {

            val startTitleText = intent.getStringExtra(ConstantValues.STARTING_CONCEPT).toString()
            val goalTitleText = intent.getStringExtra(ConstantValues.GOAL_CONCEPT).toString()

            val currentTitle = remember { mutableStateOf(startTitleText) }
            val goalTitle = remember { mutableStateOf(goalTitleText) }

            val pathList = remember { mutableStateOf(listOf(startTitleText)) }
            val totalSteps = remember { mutableStateOf(0) }
            val win = remember { mutableStateOf(false) }

            fun endQuest(win: Boolean, totalSteps: Int, pathList: List<String>) {
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra(ConstantValues.WIN, win)
                    putExtra(ConstantValues.STARTING_CONCEPT, startTitleText)
                    putExtra(ConstantValues.GOAL_CONCEPT, goalTitle.value)
                    putExtra(ConstantValues.TOTAL_STEPS, totalSteps)
                    putExtra(ConstantValues.MAX_PROGRESS, 100) // Define progressBarMaxValue as needed
                    putStringArrayListExtra(ConstantValues.PATH, ArrayList(pathList))
                }
                startActivity(intent)
                finish()
            }

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