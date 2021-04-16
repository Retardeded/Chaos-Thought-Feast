package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private lateinit var nameView: TextView
    private lateinit var scoreView: TextView

    private lateinit var buttonFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        nameView = findViewById(R.id.tv_name)
        scoreView = findViewById(R.id.tv_score)
        buttonFinish = findViewById(R.id.btn_finish)

        val userName = intent.getStringExtra(Constants.USER_NAME)
        nameView.text = userName
        val totalQuestions = intent.getIntExtra(Constants.TOTAL_QUESTIONS,0)
        val correctAnswers = intent.getIntExtra(Constants.CORRECT_ANSWERS,0)

        scoreView.text = "Your score is $correctAnswers out of $totalQuestions"

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}