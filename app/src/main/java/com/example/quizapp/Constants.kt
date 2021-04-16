package com.example.quizapp

object Constants {

    const val TITLE_START:String = "start_title"
    const val TITLE_GOAL:String = "goal_title"
    const val TOTAL_MOVES:String = "total_moves"

    fun getQuestions(): ArrayList<Question>
    {
        val questionsList = ArrayList<Question>()
        val que1 = Question(
            1,
            "What country flag",
            R.drawable.ic_flag_of_argentina,
            "Argentina",
            "Austerlia",
            "Armenia",
            "Austria",
            1
        )

        val que2 = Question(
                2,
                "What country flag",
                R.drawable.ic_flag_of_australia,
                "Argentina",
                "Australia",
                "New Zeland",
                "Austria",
                1
        )

        val que3 = Question(
                3,
                "What country flag",
                R.drawable.ic_flag_of_brazil,
                "Brazil",
                "Jamaica",
                "New Zeland",
                "Austria",
                1
        )

        questionsList.add(que1)
        questionsList.add(que2)
        questionsList.add(que3)

        return questionsList
    }
}