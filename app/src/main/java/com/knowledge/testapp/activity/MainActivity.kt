package com.knowledge.testapp.activity

import RandomArticleViewModel
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var buttonRandomStart: ImageButton
    private lateinit var buttonRandomGoal: ImageButton
    private lateinit var buttonRandomCategory: ImageButton
    private lateinit var editTextStartTitle: EditText
    private lateinit var editTextGoalTitle: EditText
    private lateinit var editTextKeyword: EditText
    private lateinit var editTextCategory: EditText
    private val webParsing = WebParsing(this)
    private lateinit var randomArticleViewModel: RandomArticleViewModel

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonRandomStart = findViewById(R.id.btn_randomStartTitle)
        buttonRandomGoal = findViewById(R.id.btn_randomGoalTitle)
        buttonRandomCategory = findViewById(R.id.btn_randomCategory)

        buttonStart = findViewById(R.id.btn_start)
        editTextStartTitle = findViewById(R.id.et_startTitle)
        editTextGoalTitle = findViewById(R.id.et_goalTitle)
        editTextKeyword = findViewById(R.id.et_Keyword)
        editTextCategory = findViewById(R.id.et_Category)

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

        editTextStartTitle.afterTextChanged { webParsing.isStartTitleCorrect(QuizValues.BASIC_LINK +it) }
        editTextGoalTitle.afterTextChanged { webParsing.isGoalTitleCorrect(QuizValues.BASIC_LINK +it) }


        buttonStart.setOnClickListener{
            if(editTextStartTitle.text.isEmpty() || editTextGoalTitle.text.isEmpty()){
                //println("empty")
                val text = "Fill in Start and Goal title!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
            else {
                if(QuizValues.correctGoal && QuizValues.correctStart) startQuizActivity()

                if(!QuizValues.correctStart)
                {
                    editTextStartTitle.setPaintFlags(editTextStartTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                }
                else
                {
                    editTextStartTitle.setPaintFlags(Paint.LINEAR_TEXT_FLAG)
                }
                if(!QuizValues.correctGoal)
                {
                    editTextGoalTitle.setPaintFlags(editTextGoalTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                }
                else
                {
                    editTextGoalTitle.setPaintFlags(Paint.LINEAR_TEXT_FLAG)
                }
            }

        }

        randomArticleViewModel = ViewModelProvider(this).get(RandomArticleViewModel::class.java)

        fun setRandomArticle(editText: EditText): Boolean {
            var success = false
            lifecycleScope.launch {
                runCatching {
                    val category = editTextCategory.text.toString()
                    val keyword = editTextKeyword.text.toString()

                    if(keyword.isNotEmpty()) {
                        randomArticleViewModel.getArticleByKeyword(keyword)
                    }
                    else if(category.isNotEmpty()) {
                        randomArticleViewModel.getRandomArticleFromCategory("Category:$category")
                    } else {
                        randomArticleViewModel.getRandomArticle()
                    }

                }.onSuccess { result ->
                    editText.setText(result)
                    success = true
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }
            }
            return success
        }

        buttonRandomStart.setOnClickListener {
            QuizValues.correctStart = setRandomArticle(editTextStartTitle)
        }

        buttonRandomGoal.setOnClickListener {
            QuizValues.correctGoal = setRandomArticle(editTextGoalTitle)
        }

        buttonRandomCategory.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    randomArticleViewModel.getRandomCategory()
                }.onSuccess { result ->
                    editTextCategory.setText(result)
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }
            }
        }

        auth = FirebaseAuth.getInstance()

        val btnLogout = findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // Call the logout function
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        // Redirect to the LoginActivity after logout
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
        intent.putExtra(QuizValues.TITLE_START, editTextStartTitle.text.toString().replace(" ", "_"))
        intent.putExtra(QuizValues.TITLE_GOAL, editTextGoalTitle.text.toString().replace(" ", "_"))
        startActivity(intent)
    }

    fun goToPreviousAttempts(view: View) {
        val intent = Intent(this, PreviousAttempts::class.java)
        startActivity(intent)
    }
}