package com.knowledge.quizapp

import android.provider.BaseColumns

object WikiPath {
    internal var TABLE_USER = "table_user"

    internal class UserColumns : BaseColumns {
        companion object {
            var STARTTITLE = "starttitle"
            var GOALTITLE = "goaltitle"
            var PATH = "path"
            var PATHLENGTH = "pathlength"
            var SUCCESS = "success"
        }

    }
}