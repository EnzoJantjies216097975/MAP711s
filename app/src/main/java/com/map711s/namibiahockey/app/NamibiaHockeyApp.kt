package com.map711s.namibiahockey.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.map711s.namibiahockey.navigation.NamibiaHockeyAppRouter
import com.map711s.namibiahockey.navigation.Screen
import com.map711s.namibiahockey.screens.SignUpScreen
import com.map711s.namibiahockey.screens.TermsAndConditionsScreen

@Composable
fun NamibiaHockeyApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = NamibiaHockeyAppRouter.currentScreen) { currentState ->
            when(currentState.value){
                is Screen.SignUpScreen ->{
                    SignUpScreen()
                }
                is Screen.TermsAndConditionsScreen ->{
                    TermsAndConditionsScreen()
                }
            }
        }
    }
}