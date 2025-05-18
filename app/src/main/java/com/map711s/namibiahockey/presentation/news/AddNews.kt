package com.map711s.namibiahockey.presentation.news

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.NewsCategory
import com.map711s.namibiahockey.data.model.NewsPiece
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNews: () -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val newsState by viewModel.newsState.collectAsState()
    val context = LocalContext.current

    // Form fields
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var publishDate by remember { mutableStateOf("") }  // Now a string, formatted from LocalDate
    var category by remember { mutableStateOf(NewsCategory.GENERAL) }
    var isBookmarked by remember { mutableStateOf(false) }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) } // LocalDate for the picker
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Format as needed

    LaunchedEffect(newsState) {
        if (newsState.newsPiece != null) {
            Toast.makeText(context, "News created successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetNewsState()
            onNavigateToNews()
        }
        if (newsState.error != null) {
            snackbarHostState.showSnackbar(newsState.error!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add News") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "News Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Title field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        placeholder = { Text("Enter news title") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content field
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") },
                        placeholder = { Text("Enter news content") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 5, // Increased maxLines for better content input
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Author Name
                    OutlinedTextField(
                        value = authorName,
                        onValueChange = { authorName = it },
                        label = { Text("Author Name") },
                        placeholder = { Text("Enter author name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Publish Date
                    OutlinedTextField(
                        value = publishDate,
                        onValueChange = { }, // Date is selected via dialog
                        label = { Text("Publish Date") },
                        placeholder = { Text("Select publish date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Select Publish Date"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle the date and format it as you need
                                    showDatePicker = false
                                    // You can use a Calendar instance to get the date
                                    val calendar = Calendar.getInstance()
                                    // Or use the date from date picker
                                    publishDate = calendar.time.toString() // hardcoded example
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            // DatePicker() // From  androidx.compose.material3.DatePicker
                            Text("Date Picker") // placeholder
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Dropdown
                    var categoryExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category.name, // Display the name of the enum
                            onValueChange = { },
                            label = { Text("Category") },
                            placeholder = { Text("Select category") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { categoryExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow"
                                    )
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            NewsCategory.entries.forEach { newsCategory ->
                                DropdownMenuItem(
                                    text = { Text(text = newsCategory.name) },
                                    onClick = {
                                        category = newsCategory
                                        categoryExpanded = false
                                    },
                                    trailingIcon = {
                                        if (category == newsCategory) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val newsPiece = NewsPiece(
                        title = title,
                        content = content,
                        authorName = authorName,
                        publishDate = publishDate,
                        category = category,
                        isBookmarked = isBookmarked
                    )
                    viewModel.createNewsPiece(newsPiece)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = title.isNotBlank() && content.isNotBlank() && authorName.isNotBlank() && publishDate.isNotBlank()
            ) {
                if (newsState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Add News",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddNewsScreenPreview() {
    // AddNewsScreen(onNavigateBack = {}, onNavigateToNews = {})
}
