package com.knowledge.testapp.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.WebParsing
import com.knowledge.testapp.utils.ModifyingStrings

class OptionsAdapter(
    private val context: Context,
    private val listener: OptionClickListener,
    private var options: List<String>,
    private val goalConcept: String,
    private val progressBar: ProgressBar,
    private val tvProgressBar: TextView,
    private val tvToFound: TextView,
    private val recyclerView: RecyclerView,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {
    private val visibleOptions: MutableList<String> = mutableListOf()
    private val visibleThreshold = 50
    val threshold = 10 // Adjust this to your desired threshold value
    var isLoadingMore = false
    var maxLoad = false
    private val webParsing: WebParsing = WebParsing()
    private var selectedPositionOption: Int = 0
    val pathList: ArrayList<String> = ArrayList()
    var totalSteps: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    init {
        val initialDataSubset = options.subList(0, minOf(visibleThreshold, options.size))
        visibleOptions.addAll(initialDataSubset)
        pathList.add(options[0])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (!isLoadingMore && !maxLoad && totalItemCount <= lastVisibleItem + visibleThreshold/2) {
                    isLoadingMore = true
                    appendMoreData()
                }
            }
        })

        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = visibleOptions[position]
        holder.bind(option)
    }

    fun appendMoreData() {
        if (maxLoad)
            return

        val currentSize = visibleOptions.size
        val startIndex = currentSize
        val endIndex = minOf(startIndex + visibleThreshold, options.size)
        val newData = options.subList(startIndex, endIndex)
        visibleOptions.addAll(newData)
        if (visibleOptions.size >= options.size) {
            Log.d("OptionsAdapter", "visibleOptions.size: ${visibleOptions.size}")
            Log.d("OptionsAdapter", "options.size: ${options.size}")
            maxLoad = true
        }

        handler.post {
            notifyDataSetChanged()
            isLoadingMore = false
        }
    }

    override fun getItemCount(): Int {
        return visibleOptions.size
    }

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOption: TextView = itemView.findViewById(R.id.tvOptionItem)
        private val tvOptionDescription: TextView = itemView.findViewById(R.id.tvOptionItemDescription)
        private val descriptionLayout: RelativeLayout = itemView.findViewById(R.id.descriptionLayout)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOptionClicked(options[position], tvOption)
                }

            }

            itemView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    // Handle the long-press on tvOption here
                    if (tvOptionDescription.visibility == View.VISIBLE) {
                        tvOptionDescription.visibility = View.GONE // Make it invisible
                    } else {
                        onOptionDescriptionClicked(tvOptionDescription, tvOption)
                        tvOptionDescription.visibility = View.VISIBLE // Make it visible
                    }
                    // Return true to indicate that the long-press event has been consumed
                    return true
                }
            })

            descriptionLayout.setOnClickListener {
                tvOptionDescription.visibility = View.GONE
            }
        }

        fun bind(option: String) {
            tvOption.text = option
            tvOptionDescription.text = ""
        }
    }

    private fun onOptionDescriptionClicked(tvDescription: TextView, tvOption: TextView) {
        val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.languageCode, tvOption.text.toString())
        tvDescription.text = webParsing.fetchAndProcessHtmlToGetParagraph(articleUrl)
    }

    private fun onOptionClicked(selectedOption: String, tv: TextView) {
        // Implement the logic for handling the clicked option here

        pathList.add(selectedOption)
        println("click path:" + pathList)
        tvToFound.text = selectedOption

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(context, R.drawable.selected_option_border_bg)

        tv.postDelayed({
            tv.setTextColor(Color.parseColor("#7A8089"))
            tv.typeface = Typeface.DEFAULT
            tv.background = ContextCompat.getDrawable(context, R.drawable.default_option_border_bg)
        }, 300) // Delay of 0.3 seconds (300 milliseconds)

        val articleUrl = ModifyingStrings.generateArticleUrl(QuizValues.USER!!.languageCode, tv.text.toString())
        webParsing.fetchAndProcessHtmlToGetTitles(articleUrl, tv) { urls ->
            options = urls
            maxLoad = false
            println(options)
            val initialDataSubset = options.subList(0, minOf(visibleThreshold, options.size))
            visibleOptions.clear()
            visibleOptions.addAll(initialDataSubset)
            notifyDataSetChanged()
            recyclerView.scrollToPosition(0)
        }

        if(tv.text == goalConcept)
        {
            totalSteps++
            listener.endQuiz(true, totalSteps, pathList)
        }
        else
        {
            totalSteps++
            progressBar.progress = totalSteps
            tvProgressBar.text = totalSteps.toString() + "/" + progressBar.max
            if(totalSteps > progressBar.max)
            {
                listener.endQuiz(false, totalSteps, pathList)
            }
        }
    }

    interface OptionClickListener {
        fun endQuiz(win: Boolean, totalSteps:Int, pathList:ArrayList<String> )
    }

}