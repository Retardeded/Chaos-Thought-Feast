package com.knowledge.testapp.activity

import com.knowledge.testapp.viewmodels.RandomArticleViewModel
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.viewmodels.WikiParseViewModel
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.ui.GameSetupScreen
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.NavigationUtils
import kotlinx.coroutines.launch


class MainGameSetupActivity : AppCompatActivity() {

    private val wikiParseViewModel = WikiParseViewModel()
    private lateinit var randomArticleViewModel: RandomArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        randomArticleViewModel = ViewModelProvider(this)[RandomArticleViewModel::class.java]

        setContent {

            val startTitleText = stringResource(R.string.example_starting_concept)
            val goalTitleText = stringResource(R.string.example_goal_concept)

            val startTitle = remember { mutableStateOf(startTitleText) }
            val goalTitle = remember { mutableStateOf(goalTitleText) }

            val selectedCategory = remember { mutableStateOf("") }
            val typedKeyword = remember { mutableStateOf("") }

            val updateStartTitle: (String) -> Unit = { newTitle ->
                startTitle.value = newTitle
            }

            val updateGoalTitle: (String) -> Unit = { newTitle ->
                goalTitle.value = newTitle
            }

            fun handleFetchRandomArticle(
                isStartTitle: Boolean,
                selectedCategory: String,
                typedKeyword: String,
                updateCorrectness: (Boolean) -> Unit
            ) {
                lifecycleScope.launch {
                    val article = if (typedKeyword.isNotEmpty()) {
                        randomArticleViewModel.getArticleByKeyword(typedKeyword)
                    } else if (selectedCategory.isNotEmpty()) {
                        randomArticleViewModel.getRandomArticleFromCategory(selectedCategory)
                    }
                    else {
                        randomArticleViewModel.getRandomWikiEntryInCorrectLanguage()
                    } ?: ""

                    if (isStartTitle) {
                        updateStartTitle(article ?: "")
                        updateCorrectness(true)
                    } else {
                        updateGoalTitle(article ?: "")
                        updateCorrectness(true)
                    }
                }
            }

            fun checkTitleCorrectness(title: String, onResult: (Boolean) -> Unit) {
                val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.language.languageCode, title)
                lifecycleScope.launch { // Use lifecycleScope in Activity or Fragment
                    val isCorrect = wikiParseViewModel.isTitleCorrect(articleUrl)
                    onResult(isCorrect)
                }
            }

            GameSetupScreen(
                gameMode = QuizValues.gameMode,
                fetchArticleDescription = { title, onDescriptionFetched ->
                    fetchArticleDescription(wikiParseViewModel, title, onDescriptionFetched)
                },
                onStartButtonClick = { gameMode, startTitle, goalTitle, typedKeyword, category, isStartTitleCorrect, isGoalTitleCorrect ->
                    handleStartButtonClick(gameMode, startTitle, goalTitle, typedKeyword, category, isStartTitleCorrect, isGoalTitleCorrect)
                },
                onGoToMainMenuActivity = { goToMainMenuActivity() },
                onGoToProfileActivity = { goToProfileActivity() },
                handleFetchRandomArticle = { isStartTitle, category, typedKeyword, updateCorrectness ->
                    handleFetchRandomArticle(isStartTitle, category, typedKeyword, updateCorrectness)
                },
                selectedCategory = selectedCategory,
                typedKeyword = typedKeyword,
                randomArticleViewModel = randomArticleViewModel,
                startTitle = startTitle,
                goalTitle = goalTitle,
                checkTitleCorrectness = { title, onResult ->
                    checkTitleCorrectness(title, onResult)
                },
            )
        }
    }

    fun fetchArticleDescription(viewModel: WikiParseViewModel, title: String, onDescriptionFetched: (String) -> Unit) {
        viewModel.viewModelScope.launch {
            val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.language.languageCode, title)
            if (viewModel.isTitleCorrect(articleUrl)) { // Assuming isTitleCorrect is a suspend function
                val articleDescription = viewModel.fetchAndProcessHtmlToGetParagraph(articleUrl)
                onDescriptionFetched(articleDescription)
            } else {
                onDescriptionFetched("The article title is not correct.")
            }
        }
    }

    private fun handleStartButtonClick(gameMode: GameMode, startTitle: String, goalTitle: String, keyword: String, category: String, isStartTitleCorrect: Boolean, isGoalTitleCorrect: Boolean) {
        when (gameMode) {
            GameMode.FIND_YOUR_LIKINGS -> {
                if (!isStartTitleCorrect || !isGoalTitleCorrect) {
                    showToast("Start and Goal titles must be correct!")
                } else {
                    startQuizActivity(startTitle, goalTitle,keyword, category, true, true)
                }
            }
            GameMode.LIKING_SPECTRUM_JOURNEY -> {
                if (!isGoalTitleCorrect) {
                    showToast("Goal title must be correct!")
                } else {
                    startQuizActivity(startTitle, goalTitle,keyword, category, false, true)
                }
            }
            GameMode.ANYFIN_CAN_HAPPEN -> {
                startQuizActivity(startTitle, goalTitle,keyword, category, false, false)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun startQuizActivity(startTitle: String, goalTitle: String, keyword: String, category: String, startingConceptPresent: Boolean, goalConceptPresent: Boolean) {
        val intent = Intent(this, GameActivity::class.java)

        lifecycleScope.launch {
            val startingConcept = if (startingConceptPresent) {
                startTitle.replace(" ", "_")
            } else {
                getRandomConcept(keyword, category).replace(" ", "_")
            }

            val goalConcept = if (goalConceptPresent) {
                goalTitle.replace(" ", "_")
            } else {
                getRandomConcept(keyword, category).replace(" ", "_")
            }

            intent.putExtra(QuizValues.STARTING_CONCEPT, startingConcept)
            intent.putExtra(QuizValues.GOAL_CONCEPT, goalConcept)
            startActivity(intent)
        }
    }

    private suspend fun getRandomConcept(keyword: String, category: String): String {
        return when {
            keyword.isNotEmpty() -> {
                randomArticleViewModel.getArticleByKeyword(keyword) ?: ""
            }
            category.isNotEmpty() -> {
                randomArticleViewModel.getRandomArticleFromCategory("Category:$category") ?: ""
            }
            else -> {
                randomArticleViewModel.getRandomWikiEntryInCorrectLanguage() ?: ""
            }
        }
    }

    fun goToMainMenuActivity() {
        NavigationUtils.goToMainMenu(this)
    }

    fun goToProfileActivity() {
        NavigationUtils.goToProfile(this)
    }
}