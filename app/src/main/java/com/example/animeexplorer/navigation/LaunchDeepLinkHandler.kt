package com.example.animeexplorer.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController

@Composable
internal fun HandleLaunchDeepLink(
    launchIntent: Intent?,
    navController: NavHostController
) {
    LaunchedEffect(launchIntent) {
        launchIntent?.let { intent ->
            navController.handleDeepLink(intent)
        }
    }
}

