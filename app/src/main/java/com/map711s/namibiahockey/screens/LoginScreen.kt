package com.map711s.namibiahockey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.navigation.NamibiaHockeyAppRouter
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.components.AlternativeLoginComponent
import com.map711s.namibiahockey.components.ButtonComponent
import com.map711s.namibiahockey.navigation.Screen
import com.map711s.namibiahockey.components.CheckboxComponent
import com.map711s.namibiahockey.components.ClickableForgotPasswordTextComponent
import com.map711s.namibiahockey.components.ClickableLoginTextComponent
import com.map711s.namibiahockey.components.ClickableNoAccountTextComponent
import com.map711s.namibiahockey.components.ClickableTextComponent
import com.map711s.namibiahockey.components.DividerTextComponent
import com.map711s.namibiahockey.components.HeadingTextComponent
import com.map711s.namibiahockey.components.MyTextFieldComponent
import com.map711s.namibiahockey.components.NormalTextComponent
import com.map711s.namibiahockey.components.PasswordTextFieldComponent

@Composable
fun LoginScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White,)
            .padding(33.dp),
        shape = RoundedCornerShape(corner = CornerSize(30.dp)),
    ) {
        Column (modifier = Modifier.fillMaxSize()){
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(
                    id = R.drawable.logotitle),
                    contentDescription =  "logo_title",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(130.dp)
                        .width(200.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.welcome))
            Spacer(modifier = Modifier.height(60.dp))

            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource(id = R.drawable.email)
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource(id = R.drawable.password)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ClickableForgotPasswordTextComponent(
                value = "",
                onTextSelected = {})


            Spacer(modifier = Modifier.height(110.dp))

            ButtonComponent(value = stringResource(R.string.login))

            Spacer(modifier = Modifier.height(20.dp))

            DividerTextComponent()

            Spacer(modifier = Modifier.height(6.dp))

            AlternativeLoginComponent()

            Spacer(modifier = Modifier.height(10.dp))

            ClickableNoAccountTextComponent(
                value = stringResource(id = R.string.go_to_register),
                onTextSelected = {NamibiaHockeyAppRouter.navigateTo(Screen.SignUpScreen)})


        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfLoginScreen(){
    LoginScreen()
}