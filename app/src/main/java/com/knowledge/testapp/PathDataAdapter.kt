package com.knowledge.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.data.WorldRecord

class PathDataAdapter(private val mData: ArrayList<WorldRecord>) : RecyclerView.Adapter<PathDataAdapter.ViewHolder>() {

    fun clearItems() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun addItem(mData: ArrayList<WorldRecord>) {
        this.mData.clear()
        this.mData.addAll(mData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_path, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.startTitle.text = item.startingConcept
        holder.goalTitle.text = item.goalConcept
        holder.path.text = item.path.joinToString(" -> ")
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var startTitle: TextView = itemView.findViewById<View>(R.id.tvTitleStart) as TextView
        internal var goalTitle: TextView = itemView.findViewById<View>(R.id.tvTitleGoal) as TextView
        internal var path: TextView = itemView.findViewById<View>(R.id.tvPath) as TextView
    }
}