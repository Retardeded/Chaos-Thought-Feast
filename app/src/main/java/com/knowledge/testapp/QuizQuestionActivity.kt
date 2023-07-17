package com.knowledge.testapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressView: TextView
    private lateinit var currentLinkView: TextView
    private lateinit var toFoundView: TextView

    private lateinit var optionOneView: TextView
    private lateinit var optionTwoView: TextView
    private lateinit var optionThreeView: TextView
    private lateinit var optionFourView: TextView
    private lateinit var optionFiveView: TextView
    private var options: ArrayList<TextView> = ArrayList()

    private lateinit var buttonNext: ImageButton
    private lateinit var buttonPrevious: ImageButton

    private var selectedPositionOption:Int = 0
    private var pathList:ArrayList<String> = ArrayList()
    private var totalMoves:Int = 0
    private lateinit var goalTitle:String
    private lateinit var startTitle:String

    private lateinit var webParsing: WebParsing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        progressBar = findViewById(R.id.progessBar)
        progressView = findViewById(R.id.tv_progress)
        currentLinkView = findViewById(R.id.tv_current_link)
        toFoundView = findViewById(R.id.tv_to_found)

        optionOneView = findViewById(R.id.tv_option_one)
        optionTwoView = findViewById(R.id.tv_option_two)
        optionThreeView = findViewById(R.id.tv_option_three)
        optionFourView = findViewById(R.id.tv_option_four)
        optionFiveView = findViewById(R.id.tv_option_five)

        buttonNext = findViewById(R.id.btn_next)
        buttonPrevious = findViewById(R.id.btn_previous)

        startTitle = intent.getStringExtra(QuizValues.TITLE_START).toString()
        goalTitle = intent.getStringExtra(QuizValues.TITLE_GOAL).toString()

        options.add(optionOneView)
        options.add(optionTwoView)
        options.add(optionThreeView)
        options.add(optionFourView)
        options.add(optionFiveView)

        for(option in options) {
            option.setOnClickListener(this)
        }
        buttonNext.setOnClickListener(this)
        buttonPrevious.setOnClickListener(this)

        toFoundView.text = toFoundView.text.toString() + goalTitle
        webParsing = WebParsing(this)
        webParsing.getHtmlFromUrl(QuizValues.BASIC_LINK + startTitle, currentLinkView, options)
        pathList.add(startTitle)

        val btnEnd = findViewById<Button>(R.id.btn_end)

        btnEnd.setOnClickListener {
            endQuiz(false)
        }
    }


    private fun defaultOptionsView(){

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.defulat_option_border_bg)
        }

    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.tv_option_one -> {
                selectedOptionView(optionOneView, 1)
            }
            R.id.tv_option_two -> {
                selectedOptionView(optionTwoView, 2)
            }
            R.id.tv_option_three -> {
                selectedOptionView(optionThreeView, 3)
            }
            R.id.tv_option_four -> {
                selectedOptionView(optionFourView, 4)

            }
            R.id.tv_option_five -> {
                selectedOptionView(optionFiveView, 5)

            }
            R.id.btn_next -> {
                webParsing.setNextLinks(options)
            }
            R.id.btn_previous -> {
                webParsing.setPreviousLinks(options)
            }
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int)
    {
        pathList.add(tv.text.toString())
        defaultOptionsView()
        selectedPositionOption = selectedOptionNum

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border_bg)


        tv.postDelayed({
            tv.setTextColor(Color.parseColor("#7A8089"))
            tv.typeface = Typeface.DEFAULT
            tv.background = ContextCompat.getDrawable(this, R.drawable.defulat_option_border_bg)
        }, 300) // Delay of 0.3 seconds (300 milliseconds)


        webParsing.getHtmlFromUrl(QuizValues.BASIC_LINK + tv.text, currentLinkView, options)

        if(tv.text == goalTitle)
        {
            totalMoves++
            endQuiz(true)
        }
        else
        {
            totalMoves++
            progressBar.progress = totalMoves
            progressView.text = totalMoves.toString() + "/" + progressBar.max
            if(totalMoves > progressBar.max)
            {
                endQuiz(false)
            }
        }

    }

    private fun endQuiz(win: Boolean) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(QuizValues.WIN, win)
        intent.putExtra(QuizValues.TITLE_START, startTitle)
        intent.putExtra(QuizValues.TITLE_GOAL, goalTitle)
        intent.putExtra(QuizValues.TOTAL_MOVES, totalMoves)
        intent.putExtra(QuizValues.MAX_PROGRESS, progressBar.max)
        intent.putStringArrayListExtra(QuizValues.MOVES, pathList)
        startActivity(intent)
        finish()
    }
}