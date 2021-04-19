package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var scoreView: TextView
    private lateinit var pathView: TextView

    private lateinit var buttonFinish: Button
    private lateinit var buttonPreviousAttempts: Button

    private var dbUserHelper: UserHelper? = null
    var startTitle:String? = ""
    var goalTitle:String? = ""
    var path:String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = intent.getStringExtra(Constants.MOVES)
        if (path != null) {
            path = path!!.dropLast(2)
        }
        val totalMoves: Int = intent.getIntExtra("totalMoves", 0)

            if(totalMoves <= 10)
                setContentView(R.layout.activity_result)
            else
                setContentView(R.layout.activity_lose)


        scoreView = findViewById(R.id.tv_score)
        pathView = findViewById(R.id.tv_path)
        buttonFinish = findViewById(R.id.btn_finish)
        buttonPreviousAttempts = findViewById(R.id.btn_attempts)

        startTitle = intent.getStringExtra(Constants.TITLE_START)
        goalTitle = intent.getStringExtra(Constants.TITLE_GOAL)


        pathView.setText(path)

            if(totalMoves <= 10)
                scoreView.text = "You found $goalTitle in $totalMoves moves"
            else
                scoreView.text = "You haven't found $goalTitle in at most 10 moves"


        saveToDb()

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        buttonPreviousAttempts.setOnClickListener{
            startActivity(Intent(this, PreviousAttempts::class.java))
            finish()
        }

    }

    fun saveToDb()
    {
        dbUserHelper = UserHelper(this)
        dbUserHelper!!.open()
        dbUserHelper!!.beginTransaction()
        val itemUser = PathItem()
        itemUser.titleStart = startTitle
        itemUser.titleGoal = goalTitle
        itemUser.path = path
        dbUserHelper!!.insert(itemUser)
        dbUserHelper!!.setTransactionSuccess()
        dbUserHelper!!.endTransaction()
        dbUserHelper!!.close()
    }
}