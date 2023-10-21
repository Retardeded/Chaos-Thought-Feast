package com.knowledge.testapp.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.knowledge.testapp.R
import com.knowledge.testapp.data.CategoryItem

class CategoriesArrayAdapter(context: Context, resource: Int, objects: List<CategoryItem>) :
    ArrayAdapter<CategoryItem>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)

        if (item?.type == CategoryItem.ItemType.CATEGORY) {
            // Apply styling for category items (change text color and set typeface to bold)
            val textView = view as TextView
            textView.setTextColor(ContextCompat.getColor(context, R.color.teal_200))
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            // Apply styling for subcategory items (if needed)
            // You can customize this part as desired
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}