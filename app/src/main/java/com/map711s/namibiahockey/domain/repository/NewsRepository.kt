package com.map711s.namibiahockey.domain.repository

import NewsPiece
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    /**
     * Create a new news piece
     * @param newsPiece The news piece to create
     * @return Result containing the ID of the created news piece or an error
     */
    suspend fun createNewsPiece(newsPiece: NewsPiece): Result<String>

    /**
     * Get a news piece by ID
     * @param newsId ID of the news piece to retrieve
     * @return Result containing the news piece or an error
     */
    suspend fun getNewsPiece(newsId: String): Result<NewsPiece>

    /**
     * Update an existing news piece
     * @param newsPiece The news piece with updated data
     * @return Result indicating success or failure
     */
    suspend fun updateNewsPiece(newsPiece: NewsPiece): Result<Unit>

    /**
     * Delete a news piece by ID
     * @param newsId ID of the news piece to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteNewsPiece(newsId: String): Result<Unit>

    /**
     * Get all news pieces
     * @return Result containing a list of all news pieces or an error
     */
    suspend fun getAllNewsPieces(): Result<List<NewsPiece>>

    /**
     * Get news pieces as a Flow
     * @return Flow of news pieces list that updates when data changes
     */
    fun getNewsPiecesFlow(): Flow<List<NewsPiece>>

    /**
     * Bookmark or unbookmark a news piece
     * @param newsId ID of the news piece
     * @param isBookmarked New bookmark state
     * @return Result indicating success or failure
     */
    suspend fun setBookmarkStatus(newsId: String, isBookmarked: Boolean): Result<Unit>
}