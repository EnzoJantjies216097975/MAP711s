package com.map711s.namibiahockey.screens.newsfeed

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.NewsCategory
import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddNews: () -> Unit,
    onNavigateToNewsDetails: (String) -> Unit,
    viewModel: NewsViewModel = hiltViewModel(),
    hockeyType: HockeyType,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Tournament", "League", "Team", "Player", "Bookmarked")
    val newsListState by viewModel.newsListState.collectAsState()
    val newsItems = newsListState.newsPieces.toMutableList()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.loadAllNewsPieces()
    }

    // If tab changes to "Bookmarked", load bookmarked news
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 5) { // "Bookmarked" tab
            viewModel.loadBookmarkedNews()
        } else if (selectedTabIndex == 0) { // "All" tab
            viewModel.loadAllNewsPieces()
        }
    }

    var isLoading =  newsListState.isLoading

    // Share news function
    fun shareNews(news: NewsPiece) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, news.title)
            putExtra(Intent.EXTRA_TEXT, "${news.title}\n\n${news.content.take(200)}...\n\nBy ${news.authorName}")
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share news via")
        startActivity(context, chooserIntent, null)
    }

    // Filtered news based on search and tab
    val filteredNews = if (searchQuery.isBlank()) { // Fixed typo: seachQuery -> searchQuery
        when (selectedTabIndex) {
            0 -> newsItems // All
            1 -> newsItems.filter { it.category == NewsCategory.TOURNAMENT }
            2 -> newsItems.filter { it.category == NewsCategory.LEAGUE }
            3 -> newsItems.filter { it.category == NewsCategory.TEAM }
            4 -> newsItems.filter { it.category == NewsCategory.PLAYER }
            else -> newsItems
        }
    } else {
        newsItems.filter {
            it.title.contains(searchQuery, ignoreCase = true) || // Fixed typo: seachQuery -> searchQuery
                    it.content.contains(searchQuery, ignoreCase = true) || // Fixed typo: seachQuery -> searchQuery
                    it.authorName.contains(searchQuery, ignoreCase = true) // Fixed typo: seachQuery -> searchQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter news */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddNews,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add News",
                    tint = Color.White
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs for filtering news
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search news...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp)
            )

            if (isLoading) { // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // News list
            else if (filteredNews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = when (selectedTabIndex) {
                            0 -> "No news items found"
                            1 -> "No tournament news found"
                            2 -> "No league news found"
                            3 -> "No team news found"
                            4 -> "No player news found"
                            5 -> "No bookmarked news found"
                            else -> "No news items found"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredNews){ news ->
                        NewsCard(
                            news = news,
                            onNewsClick = { onNavigateToNewsDetails(news.id) },
                            onShareClick = { shareNews(news) },
                            onBookmarkClick = { newsId, isBookmarked ->
                                viewModel.toggleBookmark(newsId, isBookmarked)
                            },
                        )
                    }

                    // Add some space at the bottom for the FAB
                    item{
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}



@Composable
fun NewsCard(
    news: NewsPiece,
    onNewsClick: (String) -> Unit,
    onBookmarkClick: (String, Boolean) -> Unit,
    onShareClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isBookmarked by remember { mutableStateOf(news.isBookmarked) }

    // Update local state when news changes
    LaunchedEffect(news.isBookmarked) {
        isBookmarked = news.isBookmarked
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNewsClick(news.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // News category tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = news.category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            //News title
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Author and date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author avatar (placeholder)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = news.authorName.firstOrNull()?.toString() ?: "A",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = news.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = news.publishDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // News content preview (shortened)
            Text(
                text = news.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onShareClick(news.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Read more text
                Text(
                    text = "Read more",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNewsClick(news.id) }
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = {
                        isBookmarked = !isBookmarked
                        onBookmarkClick(news.id, isBookmarked)
                    }
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun NewsCardPreview() {
//    NamibiaHockeyTheme {
//        NewsCard(
//            news = NewsItem(
//                id = "1",
//                title = "Sample News Title",
//                content = "Content",
//                authorName = " author name",
//                publishDate = "Apr 21 2025",
//                category = NewsCategory.GENERAL,
//                isBookmarked = true,
//            ),
//            onNewsClick = {},
//            onBookmarkClick = { _, _ -> },
//            onShareClick = {}
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun NewsFeedScreedPreview() {
//    NamibiaHockeyTheme {
//        NewsFeedScreen(
//            onNavigateBack = {}
//        )
//    }
//}