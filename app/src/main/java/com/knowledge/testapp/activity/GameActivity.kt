package com.knowledge.testapp.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import com.knowledge.testapp.adapters.OptionsAdapter
import com.knowledge.testapp.adapters.PathDataAdapter
import com.knowledge.testapp.databinding.ActivityGameBinding
import com.knowledge.testapp.utils.ModifyingStrings


class GameActivity : AppCompatActivity(), OptionsAdapter.OptionClickListener {


    private lateinit var binding: ActivityGameBinding
    private lateinit var goalConcept:String
    private lateinit var startingConcept:String

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OptionsAdapter

    private lateinit var webParsing: WebParsing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startingConcept = intent.getStringExtra(QuizValues.STARTING_CONCEPT).toString()
        goalConcept = intent.getStringExtra(QuizValues.GOAL_CONCEPT).toString()
        val text = binding.tvToFound.text.toString() + goalConcept
        binding.tvToFound.text = text

        recyclerView = binding.rvGameOptions

        webParsing = WebParsing()
        val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.languageCode, startingConcept)
        System.out.println("URL::" + articleUrl)

        recyclerView.layoutManager = LinearLayoutManager(this)

        webParsing.getHtmlFromUrl(articleUrl, binding.tvCurrentLink) { urls ->
            adapter = OptionsAdapter(this,this, urls, goalConcept,
                binding.progessBar, binding.tvProgress, binding.tvCurrentLink)
            adapter.notifyDataSetChanged()
            recyclerView.adapter = adapter
        }

        //recyclerView.addOnScrollListener(scrollListener)

        val btnEnd = findViewById<Button>(R.id.btn_end)

        btnEnd.setOnClickListener {
            endQuiz(false, adapter.totalSteps, adapter.pathList)
        }


    }

    /*
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            val totalItemCount = layoutManager.itemCount

            println("posss  $lastVisibleItemPosition")
            println("totalcount  $totalItemCount")

            if (lastVisibleItemPosition == totalItemCount - 1) {
                adapter.isLoadingMore = true
                adapter.appendMoreData()
            }
        }
    }

     */

    override fun endQuiz(win: Boolean, totalSteps:Int, pathList:ArrayList<String> ) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(QuizValues.WIN, win)
        intent.putExtra(QuizValues.STARTING_CONCEPT, startingConcept)
        intent.putExtra(QuizValues.GOAL_CONCEPT, goalConcept)
        intent.putExtra(QuizValues.TOTAL_STEPS, totalSteps)
        intent.putExtra(QuizValues.MAX_PROGRESS, binding.progessBar.max)
        intent.putStringArrayListExtra(QuizValues.PATH, pathList)
        startActivity(intent)
        finish()
    }
}