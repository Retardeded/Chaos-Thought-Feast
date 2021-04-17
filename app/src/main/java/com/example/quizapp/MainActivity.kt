package com.example.quizapp

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var editTextStartTitle: EditText
    private lateinit var editTextGoalTitle: EditText
    private val webParsing = WebParsing(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStart = findViewById(R.id.btn_start)
        editTextStartTitle = findViewById(R.id.et_startTitle)
        editTextGoalTitle = findViewById(R.id.et_goalTitle)

        editTextStartTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editTextGoalTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        editTextStartTitle.afterTextChanged { webParsing.isStartTitleCorrect(Constants.BASIC_LINK+it) }
        editTextGoalTitle.afterTextChanged { webParsing.isGoalTitleCorrect(Constants.BASIC_LINK+it) }


        buttonStart.setOnClickListener{
            if(editTextStartTitle.text.isEmpty() || editTextGoalTitle.text.isEmpty()){
                println("empty")
            }
            else {
                if(Constants.correctGoal && Constants.correctStart) startQuizActivity()

                if(!Constants.correctStart)
                {
                    editTextStartTitle.setPaintFlags(editTextStartTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                }
                else
                {
                    editTextStartTitle.setPaintFlags(Paint.LINEAR_TEXT_FLAG)
                }
                if(!Constants.correctGoal)
                {
                    editTextGoalTitle.setPaintFlags(editTextGoalTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                }
                else
                {
                    editTextGoalTitle.setPaintFlags(Paint.LINEAR_TEXT_FLAG)
                }
            }

        }
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun startQuizActivity() {
        val intent = Intent(this, QuizQuestionActivity::class.java)
        intent.putExtra(Constants.TITLE_START, editTextStartTitle.text.toString())
        intent.putExtra(Constants.TITLE_GOAL, editTextGoalTitle.text.toString())
        startActivity(intent)
    }
}