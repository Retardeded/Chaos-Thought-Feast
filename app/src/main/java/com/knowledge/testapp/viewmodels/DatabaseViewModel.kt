package com.knowledge.testapp.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.knowledge.testapp.data.PathRecord
import com.knowledge.testapp.localdb.UserPathDbManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {
    private val userPathDbManager: UserPathDbManager = UserPathDbManager.getInstance(application)
    val winningPathsData: MutableState<List<PathRecord>> = mutableStateOf(emptyList())
    val losingPathsData: MutableState<List<PathRecord>> = mutableStateOf(emptyList())

    fun fetchPathsForUser(userId: String, isWinning: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val paths = userPathDbManager.getPathsForUser(userId, isWinning)
            withContext(Dispatchers.Main) {
                if (isWinning) {
                    winningPathsData.value = paths
                } else {
                    losingPathsData.value = paths
                }
            }
        }
    }

    fun savePathToDatabase(userId: String, win: Boolean, startConcept: String, goalConcept: String, pathList: ArrayList<String>, pathLength: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userPathDbManager.savePathToDatabase(userId, win, startConcept, goalConcept, pathList, pathLength)
        }
    }

    fun clearUserData(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPathDbManager.clearUserData(email)
        }
    }
}