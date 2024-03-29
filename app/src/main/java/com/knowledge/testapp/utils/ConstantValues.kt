package com.knowledge.testapp.utils

import com.knowledge.testapp.data.User

object ConstantValues {
    const val BASIC_LINK_PREFIX:String = "https://"
    const val BASIC_LINK_INFIX:String = ".wikipedia.org/w/api.php?action=parse&page="
    const val BASIC_LINK_DESCRIPTION_INFIX:String = ".wikipedia.org/w/api.php?action=query&titles="
    const val BASIC_LINK_SUFFIX:String = "&prop=text&format=json"
    const val BASIC_LINK_DESCRIPTION_SUFFIX:String = "&format=json&prop=extracts&exintro=1"
    const val GAME_STATE:String = "GAME_STATE"
    const val worldRecords_FIND_YOUR_LIKINGS = "worldRecords_FIND_YOUR_LIKINGS"
    const val worldRecords_LIKING_SPECTRUM_JOURNEY = "worldRecords_LIKING_SPECTRUM_JOURNEY"
    const val worldRecords_ANYFIN_CAN_HAPPEN = "worldRecords_ANYFIN_CAN_HAPPEN"

    const val topUsers_FIND_YOUR_LIKINGS = "topUsers_FIND_YOUR_LIKINGS"
    const val topUsers_LIKING_SPECTRUM_JOURNEY = "topUsers_LIKING_SPECTRUM_JOURNEY"
    const val topUsers_ANYFIN_CAN_HAPPEN = "topUsers_ANYFIN_CAN_HAPPEN"
}