package com.knowledge.testapp.activity

import RandomArticleViewModel
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import com.knowledge.testapp.data.GameMode
import kotlinx.coroutines.launch


class MainGameSetupActivity : AppCompatActivity() {

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

    private var gameMode:GameMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_game_setup)

        // Continue with the rest of your activity setup

        buttonRandomStart = findViewById(R.id.btn_randomStartTitle)
        buttonRandomGoal = findViewById(R.id.btn_randomGoalTitle)
        buttonRandomCategory = findViewById(R.id.btn_randomCategory)

        buttonStart = findViewById(R.id.btn_start)
        editTextStartTitle = findViewById(R.id.et_startTitle)
        editTextGoalTitle = findViewById(R.id.et_goalTitle)
        editTextKeyword = findViewById(R.id.et_Keyword)
        editTextCategory = findViewById(R.id.et_Category)

        val gameModeString = intent.getStringExtra("gameMode")
        gameMode = when (gameModeString) {
            "findYourLikings" -> GameMode.FIND_YOUR_LIKINGS
            "likingSpectrumJourney" -> GameMode.LIKING_SPECTRUM_JOURNEY
            else -> GameMode.FIND_YOUR_LIKINGS // Default to a mode
        }

        when (gameMode) {
            GameMode.FIND_YOUR_LIKINGS -> {
                // Handle "Find Your Likings" mode
                buttonStart.setOnClickListener {
                    // Your first game mode logic here
                    validateAndProceedStartAndGoal()
                }
            }
            GameMode.LIKING_SPECTRUM_JOURNEY -> {
                val layoutToHide: LinearLayout = findViewById(R.id.layout_randomStartTitle)
                editTextStartTitle.text.clear()
                layoutToHide.visibility = View.GONE
                buttonStart.setOnClickListener {
                    // Your first game mode logic here
                    validateAndProceedGoal()
                }
                // Handle "Liking Spectrum Journey" mode
            }
            else -> {}
        }

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

        editTextStartTitle.afterTextChanged { webParsing.isStartTitleCorrect(QuizValues.BASIC_LINK +it.replace(" ", "_")) }
        editTextGoalTitle.afterTextChanged { webParsing.isGoalTitleCorrect(QuizValues.BASIC_LINK +it.replace(" ", "_")) }

        randomArticleViewModel = ViewModelProvider(this).get(RandomArticleViewModel::class.java)

        fun setRandomArticle(
            editText: EditText,
            onComplete: (success: Boolean) -> Unit
        ) {
            lifecycleScope.launch {
                runCatching {
                    val category = editTextCategory.text.toString()
                    val keyword = editTextKeyword.text.toString()

                    val result = if (keyword.isNotEmpty()) {
                        randomArticleViewModel.getArticleByKeyword(keyword)
                    } else if (category.isNotEmpty()) {
                        randomArticleViewModel.getRandomArticleFromCategory("Category:$category")
                    } else {
                        randomArticleViewModel.getRandomArticle()
                    }

                    result
                }.onSuccess { article ->
                    editText.setText(article)
                    onComplete(true) // Call the complete callback with success
                }.onFailure { throwable ->
                    onComplete(false) // Call the complete callback with failure
                }
            }
        }

        buttonRandomStart.setOnClickListener {
            setRandomArticle(editTextStartTitle) { success ->
                QuizValues.correctStart = success
            }
        }

        buttonRandomGoal.setOnClickListener {
            setRandomArticle(editTextGoalTitle) { success ->
                QuizValues.correctGoal = success
            }
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
    }

    fun validateAndProceedStartAndGoal() {
        if (editTextStartTitle.text.isEmpty() || editTextGoalTitle.text.isEmpty()) {
            val text = "Fill in Start and Goal title!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        } else {
            if (QuizValues.correctGoal && QuizValues.correctStart) {
                startQuizActivity(true, true)
            }

            if (!QuizValues.correctStart) {
                editTextStartTitle.setPaintFlags(editTextStartTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                editTextStartTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            }

            if (!QuizValues.correctGoal) {
                editTextGoalTitle.setPaintFlags(editTextGoalTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                editTextGoalTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            }
        }
    }

    fun validateAndProceedGoal() {
        if (editTextGoalTitle.text.isEmpty()) {
            val text = "Fill in Goal title!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        } else {
            if (QuizValues.correctGoal) {
                startQuizActivity(false, true)
            }

            if (!QuizValues.correctGoal) {
                editTextGoalTitle.setPaintFlags(editTextGoalTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                editTextGoalTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
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

    fun startQuizActivity(startingConceptPresent: Boolean, goalConceptPresent: Boolean) {
        val intent = Intent(this, QuizQuestionActivity::class.java)

        if (startingConceptPresent) {
            intent.putExtra(QuizValues.STARTING_CONCEPT, editTextStartTitle.text.toString().replace(" ", "_"))
            intent.putExtra(QuizValues.GOAL_CONCEPT, editTextGoalTitle.text.toString().replace(" ", "_"))
            startActivity(intent)
        } else if(goalConceptPresent) {
            lifecycleScope.launch {
                runCatching {
                    randomArticleViewModel.getRandomArticle()
                }.onSuccess { result ->
                    System.out.println("article:: " + result.toString().replace(" ", "_"))
                    intent.putExtra(QuizValues.STARTING_CONCEPT, result.toString().replace(" ", "_"))
                    intent.putExtra(QuizValues.GOAL_CONCEPT, editTextGoalTitle.text.toString().replace(" ", "_"))
                    startActivity(intent)
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }
            }
        } else {
            lifecycleScope.launch {
                val firstResult = runCatching {
                    randomArticleViewModel.getRandomArticle()
                }.onSuccess { result ->
                    intent.putExtra(QuizValues.STARTING_CONCEPT, result.toString().replace(" ", "_"))
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }.getOrNull()

                val secondResult = runCatching {
                    randomArticleViewModel.getRandomArticle()
                }.onSuccess { result ->
                    intent.putExtra(QuizValues.GOAL_CONCEPT, result.toString().replace(" ", "_"))
                    startActivity(intent)
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }.getOrNull()

                // Handle cases where either of the result values is null due to failure
                if (firstResult != null && secondResult != null) {
                    startActivity(intent)
                }
            }
        }
    }

    fun goToRankingsActivity(view: View) {
        val intent = Intent(this, RankingsActivity::class.java)
        startActivity(intent)
    }

    fun goToProfileActivity(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}