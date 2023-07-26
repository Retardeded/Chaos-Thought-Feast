package com.knowledge.testapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class RecyclerviewAdapter(private val c: Context) : RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>() {
    private val inflater: LayoutInflater
    private var mData = ArrayList<PathItem>()

    init {
        inflater = c.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    fun clearItems() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun addItem(mData: ArrayList<PathItem>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_path, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.startTitle.text = mData[position].titleStart
        holder.goalTitle.text = mData[position].titleGoal
        holder.path.text = mData[position].path

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var startTitle: TextView
        internal var goalTitle: TextView
        internal var path: TextView

        init {
            startTitle = itemView.findViewById<View>(R.id.tvTitleStart) as TextView
            goalTitle = itemView.findViewById<View>(R.id.tvTitleGoal) as TextView
            path = itemView.findViewById<View>(R.id.tvPath) as TextView
        }
    }
}