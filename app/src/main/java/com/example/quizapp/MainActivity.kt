package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var editTextStartTitle: EditText
    private lateinit var editTextGoalTitle: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStart = findViewById(R.id.btn_start)
        editTextStartTitle = findViewById(R.id.et_startTitle)
        editTextGoalTitle = findViewById(R.id.et_goalTitle)

        val webParsing = WebParsing(this)



        buttonStart.setOnClickListener{
            if(editTextStartTitle.text.isEmpty() || editTextGoalTitle.text.isEmpty()){
                //Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
                println("empty")
            }
            else {
                //webParsing.isTitleCorrect(editTextStartTitle.text.toString(), editTextStartTitle)
                //Thread.sleep(1_000)

                    /*
                else
                {
                    println("Wrong something")
                    Toast.makeText(getBaseContext(), "Reason can not be blank", Toast.LENGTH_SHORT).show()
                }


                     */
                    val intent = Intent(this, QuizQuestionActivity::class.java)
                    intent.putExtra(Constants.TITLE_START, editTextStartTitle.text.toString())
                    intent.putExtra(Constants.TITLE_GOAL, editTextGoalTitle.text.toString())
                    startActivity(intent)
                    finish()

            }

        }
    }
}