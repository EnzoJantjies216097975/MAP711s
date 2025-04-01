package com.map711s.namibiahockey.data.model

import java.util.Date

// News model for real-time information sharing
data class News(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val publishDate: Date = Date(),
    val imageUrl: String = "",
    val tags: List<String> = emptyList(),
    val category: NewsCategory = NewsCategory.GENERAL
)
