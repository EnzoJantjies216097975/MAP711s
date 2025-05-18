package com.map711s.namibiahockey.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.di.AuthViewModelFactory
import com.map711s.namibiahockey.presentation.auth.AuthViewModel
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    // viewModel: AuthViewModel = hiltViewModel()
) {
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())
    //val loginState = viewModel.loginState.value
    val scaleAnimation = remember { Animatable(0.8f) }

    // Animation effect
    LaunchedEffect(key1 = true) {
        // Animate the logo
        scaleAnimation.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 800
            )
        )

        // Wait a bit on the splash screen
        delay(1500)

        // Navigate to appropriate screen
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scaleAnimation.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "NAMIBIA HOCKEY UNION",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // App tagline
            Text(
                text = "Official Mobile Application",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

}

