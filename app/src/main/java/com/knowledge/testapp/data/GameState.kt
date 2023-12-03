package com.knowledge.testapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    var gameMode: GameMode = GameMode.FIND_YOUR_LIKINGS,
    var category: String = "",
    var startConcept: String = "",
    var goalConcept: String = "",
    var win: Boolean = false,
    var totalSteps: Int = 0,
    var pathList: List<String> = mutableListOf()
) : Parcelable