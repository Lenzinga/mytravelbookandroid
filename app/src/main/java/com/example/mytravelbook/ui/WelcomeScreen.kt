package com.example.mytravelbook.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytravelbook.R

@Composable
fun WelcomeScreen(
    // NEW: Pass along the current state so we can log it
    isFirstLaunch: Boolean,
    onGetStartedClicked: () -> Unit
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.welcome),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.welcome_message),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // NEW: Log the button click and the current state
                    Log.d("WelcomeScreen", "Get Started clicked. isFirstLaunch=$isFirstLaunch")
                    onGetStartedClicked()
                }
            ) {
                Text(text = stringResource(id = R.string.get_started))
            }
        }
    }
}
