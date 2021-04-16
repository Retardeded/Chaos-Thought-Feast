package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.TintableCompoundDrawablesView

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressView: TextView
    private lateinit var questionView: TextView
    private lateinit var imageView: ImageView

    private lateinit var optionOneView: TextView
    private lateinit var optionTwoView: TextView
    private lateinit var optionThreeView: TextView
    private lateinit var optionFourView: TextView

    private lateinit var buttonSubmit: Button

    private var mCurrentPosition:Int = 1
    private var mQuestionsList:ArrayList<Question>? = null
    private var mSelectedPositionOption:Int = 0
    private var mCorrectAnswers:Int = 0
    private var mUserName:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        progressBar = findViewById(R.id.progessBar)
        progressView = findViewById(R.id.tv_progress)
        questionView = findViewById(R.id.tv_question)
        imageView = findViewById(R.id.iv_image)

        optionOneView = findViewById(R.id.tv_option_one)
        optionTwoView = findViewById(R.id.tv_option_two)
        optionThreeView = findViewById(R.id.tv_option_three)
        optionFourView = findViewById(R.id.tv_option_four)

        buttonSubmit = findViewById(R.id.btn_submit)

        mUserName = intent.getStringExtra(Constants.USER_NAME)

        mQuestionsList = Constants.getQuestions()
        setQuestion()

        optionOneView.setOnClickListener(this)
        optionTwoView.setOnClickListener(this)
        optionThreeView.setOnClickListener(this)
        optionFourView.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

    }

    private fun setQuestion() {
        
        val question: Question? = mQuestionsList!![mCurrentPosition-1]

        defaultOptionsView()

        if(mCurrentPosition == mQuestionsList!!.size)
            buttonSubmit.text = "FINISH"
        else
            buttonSubmit.text = "SUBMIT"

        progressBar.progress = mCurrentPosition
        progressView.text = "$mCurrentPosition" + "/" + progressBar.max

        questionView.text = question!!.question
        imageView.setImageResource(question.image)

        optionOneView.text = question.optionOne
        optionTwoView.text = question.optionTwo
        optionThreeView.text = question.optionThree
        optionFourView.text = question.optionFour
    }

    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        options.add(optionFourView)
        options.add(optionThreeView)
        options.add(optionTwoView)
        options.add(optionOneView)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this,R.drawable.defulat_option_border_bg)
        }


    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_option_one-> {
                selectedOptionView(optionOneView, 1)
            }
            R.id.tv_option_two-> {
                selectedOptionView(optionTwoView, 2)
            }
            R.id.tv_option_three-> {
                selectedOptionView(optionThreeView, 3)
            }
            R.id.tv_option_four-> {
                selectedOptionView(optionFourView, 4)
            }
            R.id.btn_submit-> {
                if(mSelectedPositionOption == 0) {
                    mCurrentPosition++
                    when{
                        mCurrentPosition <= mQuestionsList!!.size -> {
                            setQuestion()
                        } else ->{
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                            startActivity(intent)
                            finish()
                        }
                    }
                }  else {
                    val question = mQuestionsList?.get(mCurrentPosition-1)
                    if(question!!.correctAnswer != mSelectedPositionOption) {
                        answerView(mSelectedPositionOption, R.drawable.wrong_option_border_bg)
                    }
                    else
                        mCorrectAnswers++

                    answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

                    if(mCurrentPosition == mQuestionsList!!.size) {
                        buttonSubmit.text = "FINISH"
                    } else {
                        buttonSubmit.text = "Go to next question"
                    }
                    mSelectedPositionOption = 0
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int)
    {
        when(answer){
            1->{
                optionOneView.background = ContextCompat.getDrawable(this, drawableView)
            }
            2->{
                optionTwoView.background = ContextCompat.getDrawable(this, drawableView)
            }
            3->{
                optionThreeView.background = ContextCompat.getDrawable(this, drawableView)
            }
            4->{
                optionFourView.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int)
    {
        defaultOptionsView()
        mSelectedPositionOption = selectedOptionNum

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this,R.drawable.selected_option_border_bg)
    }
}