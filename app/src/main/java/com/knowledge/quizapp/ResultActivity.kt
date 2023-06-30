package com.knowledge.quizapp

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

    private var dbWikiHelper: WikiHelper? = null
    var startTitle:String? = ""
    var goalTitle:String? = ""
    var path:String? = ""
    var win:Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = intent.getStringExtra(QuizValues.MOVES)
        if (path != null) {
            path = path!!.dropLast(2)
        }
        val totalMoves: Int = intent.getIntExtra(QuizValues.TOTAL_MOVES, 0)
        val maxProgress:Int = intent.getIntExtra(QuizValues.MAX_PROGRESS, 0)

            if(totalMoves <= maxProgress)
                setContentView(R.layout.activity_result)
            else
                setContentView(R.layout.activity_lose)


        scoreView = findViewById(R.id.tv_score)
        pathView = findViewById(R.id.tv_path)
        buttonFinish = findViewById(R.id.btn_finish)
        buttonPreviousAttempts = findViewById(R.id.btn_attempts)

        startTitle = intent.getStringExtra(QuizValues.TITLE_START)
        goalTitle = intent.getStringExtra(QuizValues.TITLE_GOAL)
        win = intent.getBooleanExtra(QuizValues.WIN, false)

        pathView.setText(path)

            if(win)
                scoreView.text = "You found $goalTitle in $totalMoves moves"
            else
                scoreView.text = "You haven't found $goalTitle"


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
        dbWikiHelper = WikiHelper(this)
        dbWikiHelper!!.open()
        dbWikiHelper!!.beginTransaction()
        val itemUser = PathItem()
        itemUser.titleStart = startTitle
        itemUser.titleGoal = goalTitle
        itemUser.path = path
        dbWikiHelper!!.insert(itemUser)
        dbWikiHelper!!.setTransactionSuccess()
        dbWikiHelper!!.endTransaction()
        dbWikiHelper!!.close()
    }
}