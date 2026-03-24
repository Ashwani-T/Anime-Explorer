package com.example.animeexplorer.core.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animeexplorer.domain.AnimeUiModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AutoAdvancePager(
    animeList: List<AnimeUiModel>,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {


    val pagerState = rememberPagerState(pageCount = { animeList.size })
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()

    val pageInteractionSource = remember { MutableInteractionSource() }
    val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

    val autoAdvance = !pagerIsDragged && !pageIsPressed

    if (autoAdvance) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % animeList.size
                pagerState.animateScrollToPage(
                    nextPage,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

            }
        }
    }



    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        HorizontalPager(
            pagerState
        ) { index ->
            AnimeHeroCard(
                anime = animeList[index],
                onAnimeClick = onAnimeClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                index = index
            )
        }
        DotsIndicator(
            totalDots = animeList.size,
            selectedIndex = pagerState.currentPage,
        )
    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeHeroCard(
    modifier: Modifier = Modifier,
    anime: AnimeUiModel,
    onAnimeClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    index: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimeItem(
            anime = anime,
            onClick = onAnimeClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = anime.description,
                color = Color(0xCCFFFFFF),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier.weight(1f)
            )
            IconButton(
                onClick = {},
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Get Detailed Synopsis")
            }


        }
    }
}

@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int) {

    Row(horizontalArrangement = Arrangement.Center) {
        repeat(totalDots) {
            val color = if (it == selectedIndex) Color.White else Color.Gray

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(if (it == selectedIndex) 9.dp else 7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun AnimeHeroCardPreview(modifier: Modifier = Modifier) {
//    AnimeHeroCard(
//        anime = sampleAnimeList[0],
//        index = 0
//    )
//}