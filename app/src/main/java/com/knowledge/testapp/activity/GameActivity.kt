package com.knowledge.testapp.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import com.knowledge.testapp.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityGameBinding


    private var options: ArrayList<TextView> = ArrayList()

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

        options.add(binding.tvOptionOne)
        options.add(binding.tvOptionTwo)
        options.add(binding.tvOptionThree)
        options.add(binding.tvOptionFour)
        options.add(binding.tvOptionFive)

        for(option in options) {
            option.setOnClickListener(this)
        }
        binding.btnNext.setOnClickListener(this)
        binding.btnPrevious.setOnClickListener(this)

        val text = binding.tvToFound.text.toString() + goalConcept
        binding.tvToFound.text = text
        webParsing = WebParsing(this)
        webParsing.getHtmlFromUrl(QuizValues.BASIC_LINK + startingConcept, binding.tvCurrentLink, options)
        pathList.add(startingConcept)

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
                selectedOptionView(binding.tvOptionOne, 1)
            }
            R.id.tv_option_two -> {
                selectedOptionView(binding.tvOptionTwo, 2)
            }
            R.id.tv_option_three -> {
                selectedOptionView(binding.tvOptionThree, 3)
            }
            R.id.tv_option_four -> {
                selectedOptionView(binding.tvOptionFour, 4)

            }
            R.id.tv_option_five -> {
                selectedOptionView(binding.tvOptionFive, 5)

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


        webParsing.getHtmlFromUrl(QuizValues.BASIC_LINK + tv.text, binding.tvCurrentLink, options)

        if(tv.text == goalConcept)
        {
            totalSteps++
            endQuiz(true)
        }
        else
        {
            totalSteps++
            binding.progessBar.progress = totalSteps
            binding.tvProgress.text = totalSteps.toString() + "/" + binding.progessBar.max
            if(totalSteps > binding.progessBar.max)
            {
                endQuiz(false)
            }
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