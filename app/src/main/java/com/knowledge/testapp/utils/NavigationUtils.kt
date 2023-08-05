package com.knowledge.testapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.knowledge.testapp.activity.MainMenuActivity
import com.knowledge.testapp.activity.ProfileActivity

object NavigationUtils {
    fun goToMainMenu(context: Context) {
        val intent = Intent(context, MainMenuActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    fun goToProfile(context: Context) {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}