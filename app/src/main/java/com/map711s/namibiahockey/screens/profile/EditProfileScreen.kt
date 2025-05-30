package com.map711s.namibiahockey.screens.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.map711s.namibiahockey.components.getRoleIcon
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val userProfileState by viewModel.userProfileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Form state
    var name by remember { mutableStateOf(userProfileState.user?.name ?: "") }
    var email by remember { mutableStateOf(userProfileState.user?.email ?: "") }
    var phone by remember { mutableStateOf(userProfileState.user?.phone ?: "") }
    var role by remember { mutableStateOf(userProfileState.user?.role ?: UserRole.PLAYER) }
    var location by remember { mutableStateOf("Windhoek, Namibia") }
    var bio by remember { mutableStateOf("") }

    // Image handling
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var currentProfileImageUrl by remember { mutableStateOf("") }

    // Dialog states
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showRoleChangeDialog by remember { mutableStateOf(false) }
    var roleExpanded by remember { mutableStateOf(false) }

    // Validation state
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    // Loading and save states
    var isSaving by remember { mutableStateOf(false) }
    val hasChanges = name != (userProfileState.user?.name ?: "") ||
            email != (userProfileState.user?.email ?: "") ||
            phone != (userProfileState.user?.phone ?: "") ||
            role != (userProfileState.user?.role ?: UserRole.PLAYER) ||
            selectedImageUri != null

    // Initialize form fields when user data loads
    LaunchedEffect(userProfileState.user) {
        userProfileState.user?.let { user ->
            name = user.name
            email = user.email
            phone = user.phone
            role = user.role
        }
    }

    LaunchedEffect(userProfileState.user) {
        userProfileState.user?.let { user ->
            name = user.name
            email = user.email
            phone = user.phone
            // REMOVED role initialization
        }
    }

    // Image selection launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Simulate image upload
            simulateImageUpload(
                onProgress = { progress -> uploadProgress = progress },
                onComplete = { downloadUrl ->
                    currentProfileImageUrl = downloadUrl
                    isUploading = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Profile picture updated!")
                    }
                },
                onStart = { isUploading = true }
            )
        }
    }

    // Permission launcher
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

    fun selectProfilePicture() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
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

    fun validateFields(): Boolean {
        var isValid = true

        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = ""
        }

        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Valid email is required"
            isValid = false
        } else {
            emailError = ""
        }

        if (phone.isBlank()) {
            phoneError = "Phone number is required"
            isValid = false
        } else {
            phoneError = ""
        }

        return isValid
    }

    fun saveProfile() {
        if (!validateFields()) return

        isSaving = true

        val updatedUser = User(
            id = userProfileState.user?.id ?: "",
            name = name.trim(),
            email = email.trim(),
            phone = phone.trim(),
            role = userProfileState.user?.role ?: UserRole.PLAYER // Keep existing role
        )

        // In a real app, you would call viewModel.updateUserProfile(updatedUser)
        scope.launch {
            // Simulate save operation
            kotlinx.coroutines.delay(1500)
            isSaving = false
            snackbarHostState.showSnackbar("Profile updated successfully!")
            kotlinx.coroutines.delay(500)
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) {
                            showDiscardDialog = true
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { saveProfile() },
                        enabled = hasChanges && !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
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
                .padding(16.dp)
        ) {
            // Profile Picture Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profile Picture",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Profile Picture with upload functionality
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }

                        // Camera button overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable { selectProfilePicture() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Picture",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Remove button
                        if (selectedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                                    .clickable { selectedImageUri = null },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Upload progress overlay
                        if (isUploading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        progress = { uploadProgress },
                                        color = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${(uploadProgress * 100).toInt()}%",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    if (isUploading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Basic Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (nameError.isNotEmpty()) nameError = ""
                        },
                        label = { Text("Full Name") },
                        placeholder = { Text("Enter your full name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name Icon"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = nameError.isNotEmpty(),
                        supportingText = if (nameError.isNotEmpty()) {
                            { Text(nameError, color = MaterialTheme.colorScheme.error) }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError.isNotEmpty()) emailError = ""
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("Enter your email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = emailError.isNotEmpty(),
                        supportingText = if (emailError.isNotEmpty()) {
                            { Text(emailError, color = MaterialTheme.colorScheme.error) }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            if (phoneError.isNotEmpty()) phoneError = ""
                        },
                        label = { Text("Phone Number") },
                        placeholder = { Text("Enter your phone number") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Phone Icon"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = phoneError.isNotEmpty(),
                        supportingText = if (phoneError.isNotEmpty()) {
                            { Text(phoneError, color = MaterialTheme.colorScheme.error) }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location (Mock field)
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        placeholder = { Text("Enter your location") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location Icon"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Role Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getRoleIcon(userProfileState.user?.role ?: UserRole.PLAYER),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Role",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = userProfileState.user?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "To change your role, please use the 'Request Role Change' option in your profile settings. All role changes must be approved by an administrator.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bio/Description - moved here from Professional Information
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        placeholder = { Text("Tell us about yourself") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // Role and Additional Information
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(20.dp)
//                ) {
//                    Text(
//                        text = "Professional Information",
//                        style = MaterialTheme.typography.titleLarge,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    // Role dropdown (only for admins to change)
//                    val currentUser = userProfileState.user
//                    val canChangeRole = currentUser?.role == UserRole.ADMIN
//
//                    Box(modifier = Modifier.fillMaxWidth()) {
//                        OutlinedTextField(
//                            value = role.name.lowercase().replaceFirstChar { it.uppercase() },
//                            onValueChange = { },
//                            label = { Text("Role") },
//                            modifier = Modifier.fillMaxWidth(),
//                            readOnly = true,
//                            enabled = canChangeRole,
//                            trailingIcon = if (canChangeRole) {
//                                {
//                                    IconButton(onClick = { roleExpanded = true }) {
//                                        Icon(
//                                            imageVector = Icons.Default.ArrowDropDown,
//                                            contentDescription = "Select Role"
//                                        )
//                                    }
//                                }
//                            } else null,
//                            supportingText = if (!canChangeRole) {
//                                { Text("Contact admin to change role", style = MaterialTheme.typography.bodySmall) }
//                            } else null
//                        )
//
//                        if (canChangeRole) {
//                            DropdownMenu(
//                                expanded = roleExpanded,
//                                onDismissRequest = { roleExpanded = false }
//                            ) {
//                                UserRole.entries.forEach { userRole ->
//                                    DropdownMenuItem(
//                                        text = { Text(userRole.name.lowercase().replaceFirstChar { it.uppercase() }) },
//                                        onClick = {
//                                            if (userRole != role) {
//                                                showRoleChangeDialog = true
//                                            }
//                                            role = userRole
//                                            roleExpanded = false
//                                        },
//                                        trailingIcon = {
//                                            if (role == userRole) {
//                                                Icon(
//                                                    imageVector = Icons.Default.Check,
//                                                    contentDescription = "Selected"
//                                                )
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bio/Description
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        placeholder = { Text("Tell us about yourself") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = { saveProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = hasChanges && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saving...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (hasChanges) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You have unsaved changes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


    // Discard Changes Dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to leave without saving?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Role Change Confirmation Dialog
    if (showRoleChangeDialog) {
        AlertDialog(
            onDismissRequest = { showRoleChangeDialog = false },
            title = { Text("Confirm Role Change") },
            text = { Text("Changing roles may affect access permissions. Are you sure you want to proceed?") },
            confirmButton = {
                Button(
                    onClick = { showRoleChangeDialog = false }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRoleChangeDialog = false
                    role = userProfileState.user?.role ?: UserRole.PLAYER
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Simulate image upload - replace with actual Firebase Storage upload
private fun simulateImageUpload(
    onProgress: (Float) -> Unit,
    onComplete: (String) -> Unit,
    onStart: () -> Unit
) {
    onStart()
    val handler = android.os.Handler(android.os.Looper.getMainLooper())
    var progress = 0f
    val updateInterval = 100L

    val progressRunnable = object : Runnable {
        override fun run() {
            progress += 0.1f
            onProgress(progress)

            if (progress >= 1.0f) {
                onComplete("https://example.com/uploaded-image-${System.currentTimeMillis()}.jpg")
            } else {
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    handler.postDelayed(progressRunnable, updateInterval)
}

private fun getRoleIcon(role: UserRole): ImageVector {
    return when (role) {
        UserRole.ADMIN -> Icons.Default.Shield
        UserRole.COACH -> Icons.Default.SportsSoccer
        UserRole.MANAGER -> Icons.Default.Badge
        UserRole.PLAYER -> Icons.Default.Person
    }
}