package com.map711s.namibiahockey.data.model

data class NewsPiece(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorName: String = "",
    val publishDate: String = "",
    val category: NewsCategory = NewsCategory.GENERAL,
    val isBookmarked: Boolean = false,
    val imageUrl: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "title" to title,
            "content" to content,
            "authorName" to authorName,
            "publishDate" to publishDate,
            "category" to category.name,
            "isBookmarked" to isBookmarked,
            "imageUrl" to imageUrl
        )
    }
}