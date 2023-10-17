package com.knowledge.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import com.knowledge.testapp.adapters.OptionsAdapter
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

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        webParsing.fetchAndProcessHtmlToGetTitles(articleUrl, binding.tvCurrentLink) { urls ->
            adapter = OptionsAdapter(this,this, urls, goalConcept,
                binding.progessBar, binding.tvProgress, binding.tvCurrentLink, recyclerView, layoutManager)
            adapter.notifyDataSetChanged()
            recyclerView.adapter = adapter
        }
        val btnEnd = findViewById<Button>(R.id.btn_end)

        btnEnd.setOnClickListener {
            endQuiz(false, adapter.totalSteps, adapter.pathList)
        }


    }

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