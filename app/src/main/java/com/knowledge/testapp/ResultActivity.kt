package com.knowledge.testapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ResultActivity : AppCompatActivity() {

    private lateinit var scoreView: TextView
    private lateinit var pathView: TextView

    private lateinit var buttonFinish: Button
    private lateinit var buttonPreviousAttempts: Button

    private var dbWikiHelper: WikiHelper? = null
    lateinit var startTitle:String
    lateinit var goalTitle:String
    var pathLength: Int = 0
    var pathList: List<String> = ArrayList()
    var win:Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pathList = intent.getStringArrayListExtra(QuizValues.MOVES)!!
        pathLength = pathList.size ?: 0
        val pathText = pathList.joinToString("->")

        win = intent.getBooleanExtra(QuizValues.WIN, false)
        val totalMoves: Int = intent.getIntExtra(QuizValues.TOTAL_MOVES, 0)

            if(win)
                setContentView(R.layout.activity_result)
            else
                setContentView(R.layout.activity_lose)


        scoreView = findViewById(R.id.tv_score)
        pathView = findViewById(R.id.tv_path)
        buttonFinish = findViewById(R.id.btn_finish)
        buttonPreviousAttempts = findViewById(R.id.btn_attempts)

        startTitle = intent.getStringExtra(QuizValues.TITLE_START).toString()
        goalTitle = intent.getStringExtra(QuizValues.TITLE_GOAL).toString()

        pathView.text = pathText

            if(win)
                scoreView.text = "You found $goalTitle in $totalMoves moves"
            else
                scoreView.text = "You haven't found $goalTitle"

            saveToDb()

        saveData(startTitle, goalTitle, pathList!!, pathList!!.size)

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        buttonPreviousAttempts.setOnClickListener{
            startActivity(Intent(this, PreviousAttempts::class.java))
            finish()
        }

    }

    fun saveData(startingConcept: String, goalConcept: String, path: List<String>, steps: Int) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ref: DatabaseReference = database.getReference("worldRecords")

        val recordRef = ref.push()
        val recordData = mapOf(
            "startingConcept" to startingConcept,
            "goalConcept" to goalConcept,
            "path" to path,
            "steps" to steps
        )

        recordRef.setValue(recordData)
            .addOnSuccessListener {
                println("Data saved successfully!")
            }
            .addOnFailureListener { error ->
                println("Error saving data: $error")
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
        itemUser.path = pathList.joinToString("->")
        itemUser.pathLength = pathLength
        itemUser.success = if (win) 1 else -1
        dbWikiHelper!!.insert(itemUser)
        dbWikiHelper!!.setTransactionSuccess()
        dbWikiHelper!!.endTransaction()
        dbWikiHelper!!.close()

        saveData(startTitle, goalTitle, pathList, pathLength)
    }
}