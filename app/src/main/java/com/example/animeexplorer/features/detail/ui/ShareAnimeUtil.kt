package com.example.animeexplorer.features.detail.ui

import android.content.Context
import android.content.Intent
import com.example.animeexplorer.core.domain.AnimeDetailUiModel

object ShareAnimeUtil {
    fun shareAnime(context: Context, anime: AnimeDetailUiModel) {
        val deepLink = "animeexlporer://animedetail?malId=${anime.id}"
        val shareText = buildString {
            append("Check out ${anime.title}!\n")
            append("Score: ${anime.score}/10\n")
            append("Tap to view details: $deepLink")
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val chooserIntent = Intent.createChooser(
            shareIntent,
            "Share ${anime.title}"
        )
        context.startActivity(chooserIntent)
    }
}

