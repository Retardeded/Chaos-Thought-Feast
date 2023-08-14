package com.knowledge.testapp

import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.User

object QuizValues {

    const val STARTING_CONCEPT:String = "start_concept"
    const val GOAL_CONCEPT:String = "goal_concept"
    const val TOTAL_STEPS:String = "total_steps"
    const val PATH:String = "path"
    const val MAX_PROGRESS:String = "max_progress"
    const val BASIC_LINK_PREFIX:String = "https://"
    const val BASIC_LINK_SUFFIX:String = ".wikipedia.org/wiki/"
    const val WIN:String = "win"
    var correctStart = true
    var correctGoal = true
    var gameMode = GameMode.FIND_YOUR_LIKINGS
    const val worldRecords_FIND_YOUR_LIKINGS = "worldRecords_FIND_YOUR_LIKINGS"
    const val worldsRecords_LIKING_SPECTRUM_JOURNEY = "worldRecords_LIKING_SPECTRUM_JOURNEY"
    const val worldsRecords_ANYFIN_CAN_HAPPEN = "worldRecords_ANYFIN_CAN_HAPPEN"

    const val topUsers_FIND_YOUR_LIKINGS = "topUsers_FIND_YOUR_LIKINGS"
    const val topUsers_LIKING_SPECTRUM_JOURNEY = "topUsers_LIKING_SPECTRUM_JOURNEY"
    const val topUsers_ANYFIN_CAN_HAPPEN = "topUsers_ANYFIN_CAN_HAPPEN"
    var USER: User? = null
}