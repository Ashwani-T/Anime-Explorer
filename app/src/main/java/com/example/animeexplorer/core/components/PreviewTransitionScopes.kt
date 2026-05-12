package com.example.animeexplorer.core.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreviewSharedTransitionContainer(
    content: @Composable SharedTransitionScope.(AnimatedContentScope) -> Unit
) {
    SharedTransitionLayout {
        AnimatedContent(
            targetState = Unit,
            label = "PreviewSharedTransitionContainer"
        ) {
            content(this@SharedTransitionLayout, this)
        }
    }
}
