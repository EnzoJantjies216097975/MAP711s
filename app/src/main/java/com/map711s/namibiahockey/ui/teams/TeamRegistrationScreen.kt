package com.map711s.namibiahockey.ui.teams

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamRegistrationScreen(
    onNavigateBack: () -> Unit,
    onTeamRegistered: () -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var coachName by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf("") }
    var teamInfo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    val divisions = listOf("Men's Premier", "Women's Premier", "Men's First", "Women's First", "Junior (U18)", "Junior (U16)", "Junior (U14)")

    var showDivisionsMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Team") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(
                        onClick = {
                            // Form validation
                            when {
                                teamName.isBlank() -> {
                                    errorMessage = "Team name is required"
                                }
                                coachName.isBlank() -> {
                                    errorMessage = "Coach name is required"
                                }
                                contactEmail.isBlank() -> {
                                    errorMessage = "Contact email is required"
                                }
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches() -> {
                                    errorMessage = "Please enter a valid email address"
                                }
                                contactPhone.isBlank() -> {
                                    errorMessage = "Contact phone is required"
                                }
                                selectedDivision.isBlank() -> {
                                    errorMessage = "Please select a division"
                                }
                                else -> {
                                    errorMessage = null
                                    isLoading = true

                                    // Simulate API call
                                    android.os.Handler().postDelayed({
                                        isLoading = false
                                        onTeamRegistered()
                                    }, 1500)
                                }
                            }
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Team Logo/Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder image
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "Upload Team Logo",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "Upload Team Logo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Error message
            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Team Name
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it; errorMessage = null },
                label = { Text("Team Name *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Coach Name
            OutlinedTextField(
                value = coachName,
                onValueChange = { coachName = it; errorMessage = null },
                label = { Text("Coach Name *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Email
            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it; errorMessage = null },
                label = { Text("Contact Email *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Phone
            OutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it; errorMessage = null },
                label = { Text("Contact Phone *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Division Dropdown
            ExposedDropdownMenuBox(
                expanded = showDivisionsMenu,
                onExpandedChange = { showDivisionsMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedDivision,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Division *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDivisionsMenu)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = showDivisionsMenu,
                    onDismissRequest = { showDivisionsMenu = false }
                ) {
                    divisions.forEach { division ->
                        DropdownMenuItem(
                            text = { Text(division) },
                            onClick = {
                                selectedDivision = division
                                showDivisionsMenu = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Team Info/Description
            OutlinedTextField(
                value = teamInfo,
                onValueChange = { teamInfo = it },
                label = { Text("Team Information") },
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button for smaller screens
            Button(
                onClick = {
                    // Form validation
                    when {
                        teamName.isBlank() -> {
                            errorMessage = "Team name is required"
                        }
                        coachName.isBlank() -> {
                            errorMessage = "Coach name is required"
                        }
                        contactEmail.isBlank() -> {
                            errorMessage = "Contact email is required"
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches() -> {
                            errorMessage = "Please enter a valid email address"
                        }
                        contactPhone.isBlank() -> {
                            errorMessage = "Contact phone is required"
                        }
                        selectedDivision.isBlank() -> {
                            errorMessage = "Please select a division"
                        }
                        else -> {
                            errorMessage = null
                            isLoading = true

                            // Simulate API call
                            android.os.Handler().postDelayed({
                                isLoading = false
                                onTeamRegistered()
                            }, 1500)
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Register Team")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}