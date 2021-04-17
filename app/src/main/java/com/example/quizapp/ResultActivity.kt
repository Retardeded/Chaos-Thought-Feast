package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private lateinit var titleStartView: TextView
    private lateinit var scoreView: TextView

    private lateinit var buttonFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val totalMoves = intent.getIntExtra(Constants.TOTAL_MOVES,0)
        if(totalMoves <= 10)
            setContentView(R.layout.activity_result)
        else
            setContentView(R.layout.activity_lose)

        titleStartView = findViewById(R.id.tv_start_title)
        scoreView = findViewById(R.id.tv_score)
        buttonFinish = findViewById(R.id.btn_finish)

        val startTitle = intent.getStringExtra(Constants.TITLE_START)
        val goalTitle = intent.getStringExtra(Constants.TITLE_GOAL)
        titleStartView.text = startTitle

        titleStartView.setText(startTitle)
        if(totalMoves <= 10)
            scoreView.text = "You found $goalTitle in $totalMoves moves"
        else
            scoreView.text = "You haven't found $goalTitle in at most 10 moves"

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}