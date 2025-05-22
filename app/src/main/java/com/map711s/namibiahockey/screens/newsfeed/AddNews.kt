package com.map711s.namibiahockey.screens.newsfeed

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.map711s.namibiahockey.components.HockeyTypeHeader
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.NewsCategory
import com.map711s.namibiahockey.data.model.NewsPiece
import com.map711s.namibiahockey.viewmodel.NewsViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNews: () -> Unit,
    hockeyType: HockeyType,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val newsState by viewModel.newsState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Form fields
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var publishDate by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(NewsCategory.GENERAL) }
    var isBookmarked by remember { mutableStateOf(false) }

    // Image upload state
    var includeImage by remember { mutableStateOf(false) } // FIXED: Added missing variable
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var imageUrl by remember { mutableStateOf("") }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Image selection launcher - FIXED: Moved before permission launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            isUploading = true

            // Upload to Firebase
            viewModel.uploadNewsImage(
                uri = it,
                onProgress = { progress -> uploadProgress = progress },
                onSuccess = { downloadUrl ->
                    imageUrl = downloadUrl
                    isUploading = false
                },
                onFailure = { exception ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Upload failed: ${exception.message}")
                    }
                    isUploading = false
                    selectedImageUri = null
                }
            )
        }
    }

    // Permission launcher for older Android versions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imageLauncher.launch("image/*")
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permission required to select images")
            }
        }
    }

    // Function to handle image selection with permissions
    fun selectImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ - No permission needed
                imageLauncher.launch("image/*")
            }
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                imageLauncher.launch("image/*")
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    // Form validation
    val isFormValid = title.isNotBlank() &&
            content.isNotBlank() &&
            authorName.isNotBlank() &&
            publishDate.isNotBlank() &&
            (!includeImage || !isUploading) // Allow submission if not including image or upload is complete

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
            // Hockey type header
            HockeyTypeHeader(hockeyType = hockeyType)

            Spacer(modifier = Modifier.height(16.dp))

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
                        label = { Text("Title *") },
                        placeholder = { Text("Enter news title") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = title.isEmpty() && newsState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content field
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content *") },
                        placeholder = { Text("Enter news content") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        minLines = 4,
                        maxLines = 8,
                        isError = content.isEmpty() && newsState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Author Name
                    OutlinedTextField(
                        value = authorName,
                        onValueChange = { authorName = it },
                        label = { Text("Author Name *") },
                        placeholder = { Text("Enter author name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = authorName.isEmpty() && newsState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Publish Date
                    OutlinedTextField(
                        value = publishDate,
                        onValueChange = { },
                        label = { Text("Publish Date *") },
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
                        singleLine = true,
                        isError = publishDate.isEmpty() && newsState.error != null
                    )

                    // Date Picker Dialog
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val localDate = Instant.ofEpochMilli(millis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                        publishDate = localDate.format(dateFormatter)
                                    }
                                    showDatePicker = false
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Dropdown
                    var categoryExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category.name,
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

                    // Optional Image Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Include Featured Image",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = includeImage,
                            onCheckedChange = {
                                includeImage = it
                                if (!it) {
                                    // Reset image state when toggled off
                                    selectedImageUri = null
                                    imageUrl = ""
                                    isUploading = false
                                    uploadProgress = 0f
                                }
                            }
                        )
                    }

                    if (includeImage) {
                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedImageUri != null) {
                            // Show selected image
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box {
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(16f / 9f),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Upload progress overlay
                                    if (isUploading) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.6f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color.White,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "Uploading ${(uploadProgress * 100).toInt()}%",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                LinearProgressIndicator(
                                                    progress = { uploadProgress },
                                                    modifier = Modifier.width(200.dp),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    // Remove image button
                                    IconButton(
                                        onClick = {
                                            selectedImageUri = null
                                            imageUrl = ""
                                            isUploading = false
                                            uploadProgress = 0f
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .background(
                                                Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(50)
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove image",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Change image button
                            OutlinedButton(
                                onClick = { selectImage() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUploading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Image")
                            }
                        } else {
                            // Image selection placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectImage() }
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add image",
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap to select image",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = {
                    val newsPiece = NewsPiece(
                        title = title,
                        content = content,
                        authorName = authorName,
                        publishDate = publishDate,
                        category = category,
                        isBookmarked = isBookmarked,
                        imageUrl = if (includeImage) imageUrl else ""
                    )
                    viewModel.createNewsPiece(newsPiece)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isFormValid && !newsState.isLoading
            ) {
                if (newsState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Publish News",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Form requirements note
            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Simulate image upload - replace with actual Firebase Storage upload
private fun simulateImageUpload(
    onProgress: (Float) -> Unit,
    onComplete: (String) -> Unit,
    onStart: () -> Unit
) {
    onStart()
    // Simulate upload progress
    val handler = android.os.Handler(android.os.Looper.getMainLooper())
    var progress = 0f
    val updateInterval = 100L // milliseconds

    val progressRunnable = object : Runnable {
        override fun run() {
            progress += 0.1f
            onProgress(progress)

            if (progress >= 1.0f) {
                // Simulate completed upload with fake URL
                onComplete("https://example.com/uploaded-image-${System.currentTimeMillis()}.jpg")
            } else {
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    handler.postDelayed(progressRunnable, updateInterval)
}

@Preview(showBackground = true)
@Composable
fun AddNewsScreenPreview() {
    // AddNewsScreen(onNavigateBack = {}, onNavigateToNews = {})
}
