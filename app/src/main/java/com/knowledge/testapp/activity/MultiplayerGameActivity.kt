package com.knowledge.testapp.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import com.knowledge.testapp.databinding.ActivityGameBinding

class MultiplayerGameActivity : AppCompatActivity() {


    private lateinit var binding: ActivityGameBinding
    private var selectedPositionOption:Int = 0
    private var pathList:ArrayList<String> = ArrayList()
    private var totalSteps:Int = 0
    private lateinit var goalConcept:String
    private lateinit var startingConcept:String

    private lateinit var webParsing: WebParsing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startingConcept = intent.getStringExtra(QuizValues.STARTING_CONCEPT).toString()
        goalConcept = intent.getStringExtra(QuizValues.GOAL_CONCEPT).toString()


        val text = binding.tvToFound.text.toString() + goalConcept
        binding.tvToFound.text = text
        webParsing = WebParsing()
        pathList.add(startingConcept)

        val btnEnd = findViewById<Button>(R.id.btn_end)

        btnEnd.setOnClickListener {
            endQuiz(false)
        }
        val gameId = intent.getStringExtra("gameId")
        if (gameId != null) {
            Toast.makeText(this, "ID $gameId", Toast.LENGTH_SHORT).show()
        }

    }

    private fun endQuiz(win: Boolean) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(QuizValues.WIN, win)
        intent.putExtra(QuizValues.STARTING_CONCEPT, startingConcept)
        intent.putExtra(QuizValues.GOAL_CONCEPT, goalConcept)
        intent.putExtra(QuizValues.TOTAL_STEPS, totalSteps)
        intent.putExtra(QuizValues.MAX_PROGRESS, binding.progessBar.max)
        intent.putStringArrayListExtra(QuizValues.PATH, pathList)
        startActivity(intent)
        finish()
    }
}