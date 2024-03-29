package com.knowledge.testapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.knowledge.testapp.activity.MainGameSetupActivity
import com.knowledge.testapp.activity.MainMenuActivity
import com.knowledge.testapp.activity.ProfileActivity
import com.knowledge.testapp.activity.RankingsActivity
import com.knowledge.testapp.data.GameState

object NavigationUtils {

    fun tryAgain(context: Context, gameState: GameState) {
        val intent = Intent(context, MainGameSetupActivity::class.java).apply {
            putExtra(ConstantValues.GAME_STATE, gameState)
        }
        context.startActivity(intent)
        (context as Activity).finish()
    }
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

    fun goToRankings(context: Context) {
        val intent = Intent(context, RankingsActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}