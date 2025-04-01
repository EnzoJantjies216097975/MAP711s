package com.map711s.namibiahockey.screens.profile

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {}