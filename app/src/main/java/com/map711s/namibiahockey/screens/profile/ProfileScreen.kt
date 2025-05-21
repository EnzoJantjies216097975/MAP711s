package com.map711s.namibiahockey.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.EventListItemView
import com.map711s.namibiahockey.data.model.ProfileInfoItem
import com.map711s.namibiahockey.data.model.SettingsItem
import com.map711s.namibiahockey.data.model.StatItem
import com.map711s.namibiahockey.data.model.StatusBadge
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val userProfileState by viewModel.userProfileState.collectAsState()
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    // State for edit mode
    var isEditMode by remember { mutableStateOf(false) }

    // Editable fields
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editPosition by remember { mutableStateOf("Forward") }
    var editTeam by remember { mutableStateOf("Windhoek Warriors") }
    var editJerseyNumber by remember { mutableStateOf("10") }

    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Load user data into editable fields when profile loads
    LaunchedEffect(userProfileState.user) {
        userProfileState.user?.let { user ->
            editName = user.name
            editEmail = user.email
            editPhone = user.phone
        }
    }

    // Snackbar for showing messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Used for showing edit success message
    LaunchedEffect(userProfileState.user) {
        if (isEditMode && userProfileState.user != null) {
            // Reset edit mode when profile updates
            isEditMode = false
            snackbarHostState.showSnackbar("Profile updated successfully!")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Navigate to settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (userProfileState.isLoading) {
                // Show loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (userProfileState.error != null) {
                // Show error state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading profile: ${userProfileState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // User profile header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile picture
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfileState.user?.name?.first().toString() ?: "U",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge,
                                fontSize = 48.sp
                            )

                            // Camera icon for changing profile picture
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "Change Profile Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // User name
                        Text(
                            text = userProfileState.user?.name ?: "User",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // User role
                        Text(
                            text = userProfileState.user?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Player",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status badge (Active, Verified, etc.)
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            StatusBadge(
                                text = "Active",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            StatusBadge(
                                text = "Verified",
                                color = Color(0xFF4CAF50) // green
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditMode) {
                            // In edit mode, show Save and Cancel buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedButton(
                                    onClick = { isEditMode = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Cancel")
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = {
                                        // Validate inputs
                                        var isValid = true

                                        if (editName.isBlank()) {
                                            nameError = "Name cannot be empty"
                                            isValid = false
                                        } else {
                                            nameError = null
                                        }

                                        if (editEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail).matches()) {
                                            emailError = "Please enter a valid email address"
                                            isValid = false
                                        } else {
                                            emailError = null
                                        }

                                        if (editPhone.isBlank() || editPhone.length < 10) {
                                            phoneError = "Please enter a valid phone number"
                                            isValid = false
                                        } else {
                                            phoneError = null
                                        }

                                        if (isValid) {
                                            // Save updated profile
                                            userProfileState.user?.let { user ->
                                                val updatedUser = User(
                                                    id = user.id,
                                                    name = editName,
                                                    email = editEmail,
                                                    phone = editPhone,
                                                    role = user.role
                                                )
                                                viewModel.updateUserProfile(updatedUser)
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Save Profile",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Save")
                                }
                            }
                        } else {
                            // Not in edit mode, show Edit Profile button
                            Button(
                                onClick = { isEditMode = true },
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile"
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(text = "Edit Profile")
                            }
                        }
                    }
                }

                // Player Stats Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Player Statistics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(label = "Games", value = "12")
                            StatItem(label = "Goals", value = "5")
                            StatItem(label = "Assists", value = "3")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(label = "Teams", value = "1")
                            StatItem(label = "Yellow Cards", value = "2")
                            StatItem(label = "Red Cards", value = "0")
                        }
                    }
                }

                // Profile details - Editable Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Personal Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Edit toggle button
                            if (!isEditMode) {
                                IconButton(onClick = { isEditMode = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isEditMode) {
                            // EDIT MODE UI

                            // Name
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Full Name") },
                                placeholder = { Text("Enter your full name") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Name"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                isError = nameError != null,
                                supportingText = {
                                    if (nameError != null) {
                                        Text(
                                            text = nameError!!,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Email
                            OutlinedTextField(
                                value = editEmail,
                                onValueChange = { editEmail = it },
                                label = { Text("Email") },
                                placeholder = { Text("Enter your email address") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                isError = emailError != null,
                                supportingText = {
                                    if (emailError != null) {
                                        Text(
                                            text = emailError!!,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Phone
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Phone") },
                                placeholder = { Text("Enter your phone number") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Phone"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                ),
                                isError = phoneError != null,
                                supportingText = {
                                    if (phoneError != null) {
                                        Text(
                                            text = phoneError!!,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Position
                            OutlinedTextField(
                                value = editPosition,
                                onValueChange = { editPosition = it },
                                label = { Text("Position") },
                                placeholder = { Text("Enter your playing position") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SportsSoccer,
                                        contentDescription = "Position"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Team
                            OutlinedTextField(
                                value = editTeam,
                                onValueChange = { editTeam = it },
                                label = { Text("Team") },
                                placeholder = { Text("Enter your team name") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = "Team"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Jersey Number
                            OutlinedTextField(
                                value = editJerseyNumber,
                                onValueChange = {
                                    // Only allow numbers
                                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                        editJerseyNumber = it
                                    }
                                },
                                label = { Text("Jersey Number") },
                                placeholder = { Text("Enter your jersey number") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Tag,
                                        contentDescription = "Jersey Number"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true
                            )

                        } else {
                            // DISPLAY MODE UI

                            // Name
                            ProfileInfoItem(
                                icon = Icons.Default.Person,
                                label = "Full Name",
                                value = userProfileState.user?.name ?: "Not provided"
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Email
                            ProfileInfoItem(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = userProfileState.user?.email ?: "Not provided"
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Phone
                            ProfileInfoItem(
                                icon = Icons.Default.Phone,
                                label = "Phone",
                                value = userProfileState.user?.phone ?: "Not provided"
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Player ID (assuming its the same as user ID)
                            ProfileInfoItem(
                                icon = Icons.Default.Badge,
                                label = "Player ID",
                                value = userProfileState.user?.id?.take(8)?.uppercase() ?: "Not available"
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Position
                            ProfileInfoItem(
                                icon = Icons.Default.SportsSoccer,
                                label = "Position",
                                value = "Forward" // This would come from player data
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Team
                            ProfileInfoItem(
                                icon = Icons.Default.Groups,
                                label = "Team",
                                value = "Windhoek Warriors" // This would come from player data
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Jersey Number
                            ProfileInfoItem(
                                icon = Icons.Default.Tag,
                                label = "Jersey Number",
                                value = "#10" // This would come from player data
                            )
                        }
                    }
                }

                // Upcoming Events
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Upcoming Events",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            TextButton(onClick = { /* View all events */ }) {
                                Text("View All")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Event items
                        EventListItemView(
                            title = "National Tournament",
                            date = "Jun 15, 2025",
                            location = "Windhoek Stadium"
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        EventListItemView(
                            title = "Team Practice",
                            date = "May 22, 2025",
                            location = "Training Grounds"
                        )
                    }
                }

                // Settings and preferences
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Settings & Preferences",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "Notification Preferences",
                            onClick = { /* Navigate to notification settings */ }
                        )

                        SettingsItem(
                            icon = Icons.Default.Lock,
                            title = "Privacy Settings",
                            onClick = { /* Navigate to privacy settings */ }
                        )

                        SettingsItem(
                            icon = Icons.AutoMirrored.Filled.Help,
                            title = "Help & Support",
                            onClick = { /* Navigate to help screen */ }
                        )

                        SettingsItem(
                            icon = Icons.Default.Info,
                            title = "About App",
                            onClick = { /* Navigate to about screen */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account Actions
                Button(
                    onClick = { showLogoutConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Logout")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Logout confirmation dialog
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.logout()
                            showLogoutConfirmDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutConfirmDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}