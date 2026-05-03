package com.example.animeexplorer.features.detail.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil.compose.AsyncImage
import com.example.animeexplorer.core.components.ArcLoader
import com.example.animeexplorer.core.domain.AnimeDetailUiModel
import com.example.animeexplorer.features.collection.data.local.entity.LibraryStatus
import com.example.animeexplorer.notifications.NotificationHelper


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailScreen(
    sheetState: SheetState? = null,
    showSheet: Boolean = false,
    onDismissSheet: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onNavigateToEpisodes: (Int) -> Unit = {},
    registerShareAction: ((() -> Unit)?) -> Unit = {}
) {
    val animeDetailViewModel: AnimeDetailViewModel = hiltViewModel()
    val uiState by animeDetailViewModel.uiState.collectAsState()
    val collectionState by animeDetailViewModel.collectionState.collectAsState()

    // Temp state for bottom sheet selections
    var tempStatus: LibraryStatus by rememberSaveable { mutableStateOf(LibraryStatus.UNLISTED) }
    var tempEpisodesCompleted by rememberSaveable { mutableIntStateOf(0) }

    // Initialize temp state from collectionState
    LaunchedEffect(collectionState) {
        if (collectionState != null) {
            tempStatus = collectionState!!.status
            tempEpisodesCompleted = collectionState!!.episodesCompleted
        } else {
            tempStatus = LibraryStatus.UNLISTED
            tempEpisodesCompleted = 0
        }
    }

    if (sheetState != null && showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState = sheetState
        ) {
            val anime = (uiState as? AnimeDetailUiState.Success)?.anime

            if (anime != null) {
                AnimeDetailBottomSheetContent(
                    isInCollection = collectionState != null,
                    currentStatus = tempStatus,
                    episodesCompleted = tempEpisodesCompleted,
                    totalEpisodes = anime.episodes,
                    onStatusSelected = { tempStatus = it },
                    onEpisodeSelected = { tempEpisodesCompleted = it },
                    onAddToCollection = {
                        // Update if already in collection, otherwise add
                        if (collectionState != null) {
                            animeDetailViewModel.updateCollection(tempStatus, tempEpisodesCompleted)
                        } else {
                            animeDetailViewModel.addToCollection(tempStatus, tempEpisodesCompleted)
                        }
                        onDismissSheet()
                    },
                    onRemoveFromCollection = {
                        animeDetailViewModel.removeFromCollection()
                        onDismissSheet()
                    }
                )
            }
        }
    }

    when (val state = uiState) {
        is AnimeDetailUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                ArcLoader()
            }
        }

        is AnimeDetailUiState.Success -> {
            val context = LocalContext.current
            val anime = state.anime
            DisposableEffect(anime) {
                registerShareAction { ShareAnimeUtil.shareAnime(context, anime) }
                onDispose { registerShareAction(null) }
            }
            AnimeDetailContent(
                anime = anime,
                modifier = Modifier,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onNavigateToEpisodes = onNavigateToEpisodes
            )
        }

        is AnimeDetailUiState.Error -> {
            AnimeDetailErrorScreen(
                message = "Unable to load Anime Details",
                onRetry = { animeDetailViewModel.retry() }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AnimeDetailContent(
    anime: AnimeDetailUiModel,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onNavigateToEpisodes: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    var isLaunchingYoutube by remember { mutableStateOf(false) }

    LifecycleResumeEffect(isLaunchingYoutube) {
        isLaunchingYoutube = false
        Log.d("TAG", "Running : ")
        onPauseOrDispose {
            isLaunchingYoutube = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        with(sharedTransitionScope) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "image/${anime.id}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(16.dp))
            )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = anime.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "title/${anime.id}"),
                    animatedVisibilityScope = animatedContentScope,
                    boundsTransform = { _, _ ->
                        tween(durationMillis = 500)
                    },
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                    placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize

                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Score",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = String.format("%.1f", anime.score),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = anime.status,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Episodes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Text(
                    text = anime.episodes.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(label = "Type", value = anime.type)
            InfoRow(label = "Year", value = anime.year.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (anime.trailerUrl.isNotBlank()) {
            val url = anime.trailerUrl.toUri()
            val videoIntent = Intent(Intent.ACTION_VIEW, url)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        try{
                            videoIntent.setPackage("com.google.android.youtube")
                            context.startActivity(videoIntent)
                        }catch(e: Exception){
                            videoIntent.setPackage(null)
                            context.startActivity(videoIntent)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    enabled = !isLaunchingYoutube,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                )
                {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Watch Trailer",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if(anime.episodes>0){
                    Button(
                        onClick = { onNavigateToEpisodes(anime.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Episodes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else if(anime.episodes>0){
            Button(
                onClick = { onNavigateToEpisodes(anime.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "View Episodes (${anime.episodes})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center.also { Arrangement.spacedBy(8.dp) }
        ){
            TextButton(onClick = {
                testNotification(context.applicationContext)
            }) {
                Text(
                    text = "Trigger Notification",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Synopsis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = anime.synopsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun testNotification(context: Context){
    val notification = NotificationHelper.buildNotification(
        context = context,
        animeId = "123",
        notificationId = 1
    )

    if(NotificationManagerCompat.from(context).areNotificationsEnabled()){
        NotificationManagerCompat.from(context).notify(1, notification)
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AnimeDetailErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailBottomSheetContent(
    isInCollection: Boolean,
    currentStatus: LibraryStatus,
    episodesCompleted: Int,
    totalEpisodes: Int,
    onStatusSelected: (LibraryStatus) -> Unit,
    onEpisodeSelected: (Int) -> Unit,
    onAddToCollection: () -> Unit,
    onRemoveFromCollection: () -> Unit,
    modifier: Modifier = Modifier
) {

    val lazyLibraryRowState = rememberLazyListState()
    var lazyEpisodeRowStatus = remember(currentStatus) {
        when (currentStatus) {
            LibraryStatus.UNLISTED,
            LibraryStatus.COMPLETED,
            LibraryStatus.ON_Hold -> false

            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Show collection status or add button
        Text(
            text = if (isInCollection) {
                currentStatus.name.replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { it.uppercase() }
            } else {
                "Add to Collection"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            state = lazyLibraryRowState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LibraryStatus.entries.size) { index ->
                val status = LibraryStatus.entries[index]
                ElevatedFilterChip(
                    selected = currentStatus == status,
                    onClick = {
                        when (status) {
                            LibraryStatus.UNLISTED -> {
                                lazyEpisodeRowStatus = false
                                onEpisodeSelected(0)
                            }

                            LibraryStatus.COMPLETED -> {
                                lazyEpisodeRowStatus = false
                                onEpisodeSelected(totalEpisodes)
                            }

                            LibraryStatus.ON_Hold -> {
                                lazyEpisodeRowStatus = false
                            }

                            else -> {
                                onEpisodeSelected(0)
                                lazyEpisodeRowStatus = true
                            }
                        }
                        onStatusSelected(status)
                    },
                    label = {
                        Text(
                            status.name.replace("_", " ").lowercase()
                                .replaceFirstChar { it.uppercase() })
                    }
                )
            }
        }



        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Episode Progress ($episodesCompleted / ${if (totalEpisodes > 0) totalEpisodes else "?"})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxEpisodes = if (totalEpisodes > 0) totalEpisodes else 0

            val startEpisode = if (episodesCompleted > 0) 1 else 0

            items(count = (maxEpisodes - startEpisode) + 1) { index ->
                val episode = index + startEpisode
                val isCompleted = episode <= episodesCompleted && episode != 0
                val isSelectedState = (episode == episodesCompleted)

                FilterChip(
                    selected = isSelectedState,
                    enabled = lazyEpisodeRowStatus,
                    onClick = { onEpisodeSelected(episode) },
                    label = {
                        Text(
                            text = episode.toString(),
                            fontWeight = if (isSelectedState) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (isCompleted) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isInCollection) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddToCollection,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }

                Button(
                    onClick = onRemoveFromCollection,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Remove")
                }
            }
        } else {
            Button(
                onClick = { onAddToCollection() },
                enabled = currentStatus != LibraryStatus.UNLISTED,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Collection")
            }
        }
    }
}
