package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var editTextName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStart = findViewById(R.id.btn_start)
        editTextName = findViewById(R.id.et_name)
        buttonStart.setOnClickListener{
            if(editTextName.text.isEmpty()){
                Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, QuizQuestionActivity::class.java)
                intent.putExtra(Constants.USER_NAME, editTextName.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }
}