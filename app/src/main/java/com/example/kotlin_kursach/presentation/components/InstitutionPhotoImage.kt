package com.example.kotlin_kursach.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.kotlin_kursach.BuildConfig
import com.example.kotlin_kursach.domain.model.InstitutionPhoto

/**
 * Подставляет хост из api.base.url для /uploads/... — сервер может вернуть 10.0.2.2.
 */
fun resolvePhotoUrl(url: String): String {
    if (url.startsWith("content://") || url.startsWith("file://")) return url
    if (url.startsWith("http://") || url.startsWith("https://")) {
        val pathIndex = url.indexOf("/uploads/")
        if (pathIndex >= 0) {
            val path = url.substring(pathIndex)
            return BuildConfig.API_BASE_URL.trimEnd('/') + path
        }
        return url
    }
    if (url.startsWith("/uploads/")) {
        return BuildConfig.API_BASE_URL.trimEnd('/') + url
    }
    return url
}

@Composable
fun InstitutionPhotoImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val request = remember(url, context) {
        val data: Any = when {
            url.startsWith("content://") || url.startsWith("file://") -> Uri.parse(url)
            else -> resolvePhotoUrl(url)
        }
        ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build()
    }

    SubcomposeAsyncImage(
        model = request,
        contentDescription = null,
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = contentScale,
        loading = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        },
        error = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.BrokenImage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        success = {
            SubcomposeAsyncImageContent()
        },
    )
}

@Composable
fun InstitutionPhotoImage(
    photo: InstitutionPhoto,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    InstitutionPhotoImage(url = photo.url, modifier = modifier, contentScale = contentScale)
}
