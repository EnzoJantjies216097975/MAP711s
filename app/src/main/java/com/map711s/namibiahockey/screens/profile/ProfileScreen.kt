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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.map711s.namibiahockey.components.RoleChangeRequestDialog
import com.map711s.namibiahockey.components.getRoleIcon
import com.map711s.namibiahockey.data.model.RequestStatus
import com.map711s.namibiahockey.data.model.RoleChangeRequest
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.utils.DateUtils
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.RoleChangeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToPlayerManagement: () -> Unit = {},
    onNavigateToTeamManagement: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRoleChangeRequests: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    roleChangeViewModel: RoleChangeViewModel = hiltViewModel()
) {
    val userProfileState by viewModel.userProfileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Profile picture state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    val roleChangeUiState by roleChangeViewModel.uiState.collectAsState()
    val userRequests by roleChangeViewModel.userRequests.collectAsState()
    val pendingRequestsCount by roleChangeViewModel.pendingRequestsCount.collectAsState()

    // Dialog states
    var showRoleChangeDialog by remember { mutableStateOf(false) }

    // Image selection launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Here you would typically upload to Firebase Storage
            // For now, we'll just simulate upload
            simulateImageUpload(
                onProgress = { progress -> uploadProgress = progress },
                onComplete = {
                    isUploading = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Profile picture updated!")
                    }
                },
                onStart = { isUploading = true }
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

    LaunchedEffect(userProfileState.user?.id) {
        userProfileState.user?.id?.let { userId ->
            roleChangeViewModel.loadUserRequests(userId)
        }
    }

    // Load user profile when screen is displayed
    LaunchedEffect(Unit) {
        // Profile is loaded automatically in the AuthViewModel
    }

    // Handle role change request results
    LaunchedEffect(roleChangeUiState.error, roleChangeUiState.successMessage) {
        roleChangeUiState.error?.let {
            snackbarHostState.showSnackbar(it)
            roleChangeViewModel.resetUiState()
        }
        roleChangeUiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            roleChangeViewModel.resetUiState()
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
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }

                    // Admin badge with notification for pending requests
                    if (userProfileState.user?.role == UserRole.ADMIN && pendingRequestsCount > 0) {
                        Box {
                            IconButton(onClick = onNavigateToRoleChangeRequests) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = "Role Change Requests"
                                )
                            }

                            // Notification badge
                            if (pendingRequestsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            MaterialTheme.colorScheme.error,
                                            CircleShape
                                        )
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (pendingRequestsCount > 9) "9+" else pendingRequestsCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (userProfileState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val user = userProfileState.user

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Header
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

                            // Upload progress overlay
                            if (isUploading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { uploadProgress },
                                        color = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // User Name and Role
                        Text(
                            text = user?.name ?: "User Name",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Role Badge
                        Surface(
                            color = when (user?.role) {
                                UserRole.ADMIN -> MaterialTheme.colorScheme.errorContainer
                                UserRole.COACH -> MaterialTheme.colorScheme.primaryContainer
                                UserRole.MANAGER -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.tertiaryContainer
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (user?.role) {
                                        UserRole.ADMIN -> Icons.Default.Shield
                                        UserRole.COACH -> Icons.Default.SportsSoccer
                                        UserRole.MANAGER -> Icons.Default.Badge
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = when (user?.role) {
                                        UserRole.ADMIN -> MaterialTheme.colorScheme.onErrorContainer
                                        UserRole.COACH -> MaterialTheme.colorScheme.onPrimaryContainer
                                        UserRole.MANAGER -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = user?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Player",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = when (user?.role) {
                                        UserRole.ADMIN -> MaterialTheme.colorScheme.onErrorContainer
                                        UserRole.COACH -> MaterialTheme.colorScheme.onPrimaryContainer
                                        UserRole.MANAGER -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                                    }
                                )
                            }
                        }

                        // Member since
                        Text(
                            text = "Member since ${SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Role-specific Quick Actions
                if (user?.role != UserRole.PLAYER) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Quick Actions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Player Management
                                QuickActionCard(
                                    icon = Icons.Default.Person,
                                    title = "Players",
                                    description = "Manage players",
                                    onClick = onNavigateToPlayerManagement,
                                    modifier = Modifier.weight(1f)
                                )

                                // Team Management
                                QuickActionCard(
                                    icon = Icons.Default.Groups,
                                    title = "Teams",
                                    description = "Manage teams",
                                    onClick = onNavigateToTeamManagement,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Statistics/Achievements (Mock data)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(label = "Events Created", value = "12")
                            StatItem(label = "Teams Managed", value = "3")
                            StatItem(label = "Active Players", value = "45")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Achievement progress
                        Column {
                            Text(
                                text = "Next Achievement",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Event Master",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Create 20 events (12/20)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    LinearProgressIndicator(
                                        progress = { 0.6f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add Role Management Section
                if (userProfileState.user != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    RoleManagementSection(
                        user = userProfileState.user!!,
                        userRequests = userRequests,
                        onRequestRoleChange = { showRoleChangeDialog = true },
                        isAdmin = userProfileState.user?.role == UserRole.ADMIN
                    )
                }

                // Personal Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Email
                        ProfileInfoItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = user?.email ?: "Not provided"
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Phone
                        ProfileInfoItem(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = user?.phone ?: "Not provided"
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Location (Mock)
                        ProfileInfoItem(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                            value = "Windhoek, Namibia"
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Member ID
                        ProfileInfoItem(
                            icon = Icons.Default.Badge,
                            label = "Member ID",
                            value = user?.id?.take(8)?.uppercase() ?: "N/A"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Settings & Preferences",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        SettingsItem(
                            icon = Icons.Default.Edit,
                            title = "Edit Profile",
                            onClick = onNavigateToEditProfile
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            onClick = { /* Navigate to notification settings */ }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        SettingsItem(
                            icon = Icons.Default.Settings,
                            title = "App Settings",
                            onClick = onNavigateToSettings
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = {
                        viewModel.logout()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Role Change Request Dialog
        if (showRoleChangeDialog && userProfileState.user != null) {
            RoleChangeRequestDialog(
                currentRole = userProfileState.user!!.role,
                onDismiss = { showRoleChangeDialog = false },
                onSubmitRequest = { requestedRole, reason ->
                    roleChangeViewModel.requestRoleChange(requestedRole, reason)
                    showRoleChangeDialog = false
                },
                isLoading = roleChangeUiState.isLoading
            )
        }
    }

}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Simulate image upload - replace with actual Firebase Storage upload
private fun simulateImageUpload(
    onProgress: (Float) -> Unit,
    onComplete: () -> Unit,
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
                onComplete()
            } else {
                handler.postDelayed(this, updateInterval)
            }
        }
    }

    handler.postDelayed(progressRunnable, updateInterval)
}

@Composable
private fun RoleManagementSection(
    user: User,
    userRequests: List<RoleChangeRequest>,
    onRequestRoleChange: () -> Unit,
    isAdmin: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Role Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Current Role Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getRoleIcon(user.role),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Current Role",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = user.role.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Request Role Change Button (not for admins)
                if (!isAdmin) {
                    Button(
                        onClick = onRequestRoleChange,
                        enabled = !userRequests.any { it.status == RequestStatus.PENDING }
                    ) {
                        Text("Request Change")
                    }
                }
            }

            // Show pending/recent requests
            if (userRequests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Recent Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                userRequests.take(3).forEach { request ->
                    RoleRequestStatusItem(request = request)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RoleRequestStatusItem(request: RoleChangeRequest) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when (request.status) {
            RequestStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            RequestStatus.APPROVED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            RequestStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.getRoleChangeDescription(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    color = when (request.status) {
                        RequestStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        RequestStatus.APPROVED -> MaterialTheme.colorScheme.primary
                        RequestStatus.REJECTED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outline
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = request.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = DateUtils.formatDisplayDate(request.requestedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            if (request.adminResponse.isNotBlank()) {
                Text(
                    text = "Admin: ${request.adminResponse}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier
                .size(16.dp)
                .rotate(180f) // Point right
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}