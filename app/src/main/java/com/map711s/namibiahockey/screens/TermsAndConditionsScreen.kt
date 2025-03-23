package com.map711s.namibiahockey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.components.HeadingTextComponent
import com.map711s.namibiahockey.navigation.NamibiaHockeyAppRouter
import com.map711s.namibiahockey.navigation.Screen
import com.map711s.namibiahockey.navigation.SystemBackButtonHandler

@Composable
fun TermsAndConditionsScreen() {
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(16.dp)) {

        Spacer(modifier = Modifier.height(80.dp))

        HeadingTextComponent(value = stringResource(id = R.string.terms_and_conditions_heading))
    }

    SystemBackButtonHandler {
        NamibiaHockeyAppRouter.navigateTo(Screen.SignUpScreen)
    }
}

@Preview
@Composable
fun TermsAndConditionsScreenPreview(){
    TermsAndConditionsScreen()
}