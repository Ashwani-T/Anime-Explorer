package com.example.animeexplorer.core.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.animeexplorer.ui.theme.AppTheme

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreviewSharedTransitionContainer(
    content: @Composable SharedTransitionScope.(AnimatedContentScope) -> Unit
) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SharedTransitionLayout {
                AnimatedContent(
                    targetState = Unit,
                    label = "PreviewSharedTransitionContainer"
                ) {
                    content(this@SharedTransitionLayout, this)
                }
            }
        }
    }
}
