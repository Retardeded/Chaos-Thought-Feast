package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.R
import com.knowledge.testapp.RecyclerviewAdapter
import com.knowledge.testapp.WikiHelper
import com.knowledge.testapp.fragment.TopUsersDialogFragment
import com.knowledge.testapp.fragment.PathsDialogFragment


class PreviousAttempts : AppCompatActivity() {
    private lateinit var wikiHelper: WikiHelper
    private lateinit var buttonFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_attempts)
        buttonFinish = findViewById(R.id.btn_try_again)
        setupRecyclerViews()

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerViews() {
        wikiHelper = WikiHelper(this)
    }

    fun clearPaths(view: View) {
        wikiHelper.clearTableUser()
    }

    fun showTopUsersDialog(view: View) {
        val dialogFragment = TopUsersDialogFragment()
        dialogFragment.show(supportFragmentManager, "TopUsersDialog")
    }

    fun showWinningPathsDialog(view: View) {
        val winningPathsData = wikiHelper.getSuccessfulPaths() // Replace this with your actual logic to retrieve winning paths data
        val dialogFragment = PathsDialogFragment(winningPathsData, true)
        dialogFragment.show(supportFragmentManager, "PathsDialogFragmentTag")
    }

    fun showLosingPathsDialog(view: View) {
        val losingPathsData = wikiHelper.getUnsuccessfulPaths() // Replace this with your actual logic to retrieve winning paths data
        val dialogFragment = PathsDialogFragment(losingPathsData, false)
        dialogFragment.show(supportFragmentManager, "PathsDialogFragmentTag")
    }
}