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
import com.knowledge.testapp.databinding.ActivityMainGameSetupBinding
import com.knowledge.testapp.utils.ModifyingStrings
import kotlinx.coroutines.launch


class MainGameSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainGameSetupBinding
    private val webParsing = WebParsing()
    private lateinit var randomArticleViewModel: RandomArticleViewModel

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainGameSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (QuizValues.gameMode) {
            GameMode.FIND_YOUR_LIKINGS -> {
                binding.svGameSetup.setBackgroundResource(R.drawable.findyourlikings)
                binding.tvGameModeTitle.text = getString(R.string.find_your_likings);
                binding.btnStart.setOnClickListener {
                    validateStartAndGoalAndProceed()
                }
            }
            GameMode.LIKING_SPECTRUM_JOURNEY -> {
                binding.svGameSetup.setBackgroundResource(R.drawable.likingspecturmjourney2)
                binding.tvGameModeTitle.text = getString(R.string.liking_spectrum_journey);
                val layoutStartConceptToHide: LinearLayout = findViewById(R.id.layout_randomStartTitle)
                layoutStartConceptToHide.visibility = View.GONE
                binding.etStartTitle.text.clear()
                binding.btnStart.setOnClickListener {
                    // Your first game mode logic here
                    validateGoalAndProceed()
                }
            }
            GameMode.ANYFIN_CAN_HAPPEN -> {
                binding.svGameSetup.setBackgroundResource(R.drawable.anyfin_can_happen)
                binding.tvGameModeTitle.text = getString(R.string.anyfin_can_happen);
                val layoutStartConceptToHide: LinearLayout = findViewById(R.id.layout_randomStartTitle)
                layoutStartConceptToHide.visibility = View.GONE
                val layoutGoalConceptToHide: LinearLayout = findViewById(R.id.layout_randomGoalTitle)
                layoutGoalConceptToHide.visibility = View.GONE
                binding.etStartTitle.text.clear()
                binding.etGoalTitle.text.clear()
                binding.btnStart.setOnClickListener {
                    proceed()
                }
            }
            else -> {}
        }

        binding.etStartTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etGoalTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etStartTitle.afterTextChanged { text ->
            val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.languageCode, text)

            webParsing.isTitleCorrect(articleUrl) { isCorrect ->
                // Update QuizValues or perform other actions based on isCorrect
                QuizValues.correctStart = isCorrect
                // Perform other tasks if needed
            }
        }

        binding.etGoalTitle.afterTextChanged { text ->
            val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.languageCode, text)

            webParsing.isTitleCorrect(articleUrl) { isCorrect ->
                // Update QuizValues or perform other actions based on isCorrect
                QuizValues.correctGoal = isCorrect
                // Perform other tasks if needed
            }
        }
        randomArticleViewModel = ViewModelProvider(this)[RandomArticleViewModel::class.java]

        fun setRandomArticle(
            editText: EditText,
            onComplete: (success: Boolean) -> Unit
        ) {
            lifecycleScope.launch {
                runCatching {
                    val category = binding.etCategory.text.toString()
                    val keyword = binding.etKeyword.text.toString()

                    val result = if (keyword.isNotEmpty()) {
                        randomArticleViewModel.getArticleByKeyword(keyword)
                    } else if (category.isNotEmpty()) {
                        randomArticleViewModel.getRandomArticleFromCategory(category)
                    } else {
                        randomArticleViewModel.getRandomWikiEntry()
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

        binding.btnRandomStartTitle.setOnClickListener {
            setRandomArticle(binding.etStartTitle) { success ->
                QuizValues.correctStart = success
            }
        }

        binding.btnRandomGoalTitle.setOnClickListener {
            setRandomArticle(binding.etGoalTitle) { success ->
                QuizValues.correctGoal = success
            }
        }

        binding.btnRandomCategory.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    randomArticleViewModel.getRandomCategory()
                }.onSuccess { result ->
                    binding.etCategory.setText(result)
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }
            }
        }

        auth = FirebaseAuth.getInstance()
    }

    fun validateStartAndGoalAndProceed() {
        if (binding.etStartTitle.text.isEmpty() || binding.etGoalTitle.text.isEmpty()) {
            val text = "Fill in Start and Goal title!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        } else {
            if (QuizValues.correctGoal && QuizValues.correctStart) {
                startQuizActivity(true, true)
            }

            if (!QuizValues.correctStart) {
                binding.etStartTitle.paintFlags = binding.etStartTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.etStartTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            }

            if (!QuizValues.correctGoal) {
                binding.etGoalTitle.paintFlags = binding.etGoalTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.etGoalTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            }
        }
    }

    fun validateGoalAndProceed() {
        if (binding.etGoalTitle.text.isEmpty()) {
            val text = "Fill in Goal title!"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        } else {
            if (QuizValues.correctGoal) {
                startQuizActivity(false, true)
            }

            if (!QuizValues.correctGoal) {
                binding.etGoalTitle.paintFlags = binding.etGoalTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.etGoalTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            }
        }
    }

    fun proceed() {
        startQuizActivity(false, false)
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
        val intent = Intent(this, GameActivity::class.java)

        if (startingConceptPresent) {
            intent.putExtra(QuizValues.STARTING_CONCEPT, binding.etStartTitle.text.toString().replace(" ", "_"))
            intent.putExtra(QuizValues.GOAL_CONCEPT, binding.etGoalTitle.text.toString().replace(" ", "_"))
            startActivity(intent)
        } else if(goalConceptPresent) {
            lifecycleScope.launch {
                runCatching {
                    randomArticleViewModel.getRandomWikiEntry()
                }.onSuccess { result ->
                    intent.putExtra(QuizValues.STARTING_CONCEPT, result.toString().replace(" ", "_"))
                    intent.putExtra(QuizValues.GOAL_CONCEPT, binding.etGoalTitle.text.toString().replace(" ", "_"))
                    startActivity(intent)
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }
            }
        } else {

            lifecycleScope.launch {
                val firstResult = runCatching {
                    val category = binding.etCategory.text.toString()
                    val keyword = binding.etKeyword.text.toString()

                    val result = if (keyword.isNotEmpty()) {
                        randomArticleViewModel.getArticleByKeyword(keyword)
                    } else if (category.isNotEmpty()) {
                        randomArticleViewModel.getRandomArticleFromCategory("Category:$category")
                    } else {
                        randomArticleViewModel.getRandomWikiEntry()
                    }

                    result
                }.onSuccess { result ->
                    intent.putExtra(QuizValues.STARTING_CONCEPT, result.toString().replace(" ", "_"))
                }.onFailure { throwable ->
                    // Handle the error, e.g., show an error message
                }.getOrNull()

                val secondResult = runCatching {
                    val category = binding.etCategory.text.toString()
                    val keyword = binding.etKeyword.text.toString()

                    val result = if (keyword.isNotEmpty()) {
                        randomArticleViewModel.getArticleByKeyword(keyword)
                    } else if (category.isNotEmpty()) {
                        randomArticleViewModel.getRandomArticleFromCategory("Category:$category")
                    } else {
                        randomArticleViewModel.getRandomArticle()
                    }

                    result
                }.onSuccess { result ->
                    intent.putExtra(QuizValues.GOAL_CONCEPT, result.toString().replace(" ", "_"))
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

    fun goToMainMenuActivity(view: View) {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }

    fun goToProfileActivity(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}