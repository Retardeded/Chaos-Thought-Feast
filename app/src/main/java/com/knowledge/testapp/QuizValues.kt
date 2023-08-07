package com.knowledge.testapp

import com.knowledge.testapp.data.GameMode

object QuizValues {

    const val STARTING_CONCEPT:String = "start_concept"
    const val GOAL_CONCEPT:String = "goal_concept"
    const val TOTAL_STEPS:String = "total_steps"
    const val PATH:String = "path"
    const val MAX_PROGRESS:String = "max_progress"
    const val BASIC_LINK:String = "https://en.wikipedia.org/wiki/"
    const val WIN:String = "win"
    var correctStart = true
    var correctGoal = true
    var gameMode = GameMode.FIND_YOUR_LIKINGS
}