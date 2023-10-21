package com.knowledge.testapp.data

class CategoryItem(val name: String, val type: ItemType) {
    enum class ItemType {
        CATEGORY,
        SUBCATEGORY
    }

    override fun toString(): String {
        return name // Return the category name for display
    }
}