package com.example.animeexplorer.features.search.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp


@Composable
fun GenreChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Surface(
        modifier = Modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF9EA7E5) else Color.Gray.copy(alpha = 0.5f)
        ),
        color = if (isSelected) Color(0xFF9EA7E5).copy(alpha = 0.2f) else Color.Transparent
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color(0xFF9EA7E5) else MaterialTheme.colorScheme.onSurface
        )
    }
}