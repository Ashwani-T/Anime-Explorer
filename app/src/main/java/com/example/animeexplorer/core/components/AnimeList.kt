package com.example.animeexplorer.core.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animeexplorer.core.domain.AnimeUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeItem(
    anime: AnimeUiModel,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onClick: (Int) -> Unit = {},
    contentScale: ContentScale = ContentScale.Fit,
    isHorizontalPage : Boolean = false
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(anime.id) }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            with(sharedTransitionScope) {
                Box {
                    AsyncImage(
                        model = anime.imageUrl,
                        contentDescription = anime.title,
                        contentScale = contentScale,
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "image/${anime.id}"),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .fillMaxWidth()
                            .then(
                                if (isHorizontalPage) Modifier.heightIn(max = 360.dp).wrapContentHeight()
                                else Modifier.heightIn(max = 180.dp).wrapContentHeight()
                            )
                    )

                    RatingBadge(anime.score)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = anime.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "title/${anime.id}"),
                                animatedVisibilityScope = animatedContentScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 500)
                                },
                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                                placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize

                            )
                            .weight(1f).testTag("anime_title")
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBadge(
    rating: Double,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(4.dp)
            .background(Color(0xFF6A5BE2), RoundedCornerShape(12.dp))

    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(start = 4.dp, end = 2.dp)
                .size(14.dp)
        )
        Text(
            "$rating",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 0.dp, top = 0.dp, bottom = 0.dp, end = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBadgePreview(modifier: Modifier = Modifier) {
    RatingBadge(rating = 8.5)
}

@Preview(showBackground = true)
@Composable
fun AnimeItemPreview() {
    val sampleAnime = AnimeUiModel(
        id = 1,
        title = "Attack on Titan",
        imageUrl = "https://myanimelist.net/images/anime/4/19644l.jpg",
        score = 9.0,
        description = "Sample Description",
        duration = "0"
    )
    PreviewSharedTransitionContainer { animatedContentScope ->
        AnimeItem(
            anime = sampleAnime,
            sharedTransitionScope = this,
            animatedContentScope = animatedContentScope,
            onClick = {}
        )
    }
}