package com.example.kotlin_kursach.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.kotlin_kursach.domain.model.InstitutionPhoto

@Composable
fun InstitutionPhotoGallery(
    photos: List<InstitutionPhoto>,
    modifier: Modifier = Modifier,
) {
    if (photos.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(photos, key = { it.id }) { photo ->
            InstitutionPhotoImage(
                photo = photo,
                modifier = Modifier
                    .size(width = 220.dp, height = 140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun InstitutionPhotoThumbnail(
    photoUrl: String,
    modifier: Modifier = Modifier,
) {
    InstitutionPhotoImage(
        url = photoUrl,
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun InstitutionPhotoPlaceholder(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "Нет фото",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun InstitutionPhotosCarousel(
    photos: List<InstitutionPhoto>,
    modifier: Modifier = Modifier,
) {
    if (photos.isEmpty()) {
        InstitutionPhotoPlaceholder(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
        )
        return
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(photos, key = { it.id }) { photo ->
            InstitutionPhotoImage(
                photo = photo,
                modifier = Modifier
                    .size(width = 280.dp, height = 180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
