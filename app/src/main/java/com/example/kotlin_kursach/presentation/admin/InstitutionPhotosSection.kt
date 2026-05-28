package com.example.kotlin_kursach.presentation.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.kotlin_kursach.domain.model.InstitutionPhoto
import com.example.kotlin_kursach.presentation.components.InstitutionPhotoImage

@Composable
fun InstitutionPhotosSection(
    photos: List<InstitutionPhoto>,
    photoUrlInput: String,
    isPhotoProcessing: Boolean,
    onPhotoUrlInputChange: (String) -> Unit,
    onAddPhotoUrl: () -> Unit,
    onPickPhoto: (Uri) -> Unit,
    onRemovePhoto: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            onPickPhoto(uri)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Фотографии (необязательно)",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Можно оставить заведение без фото",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (photos.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photos, key = { it.id }) { photo ->
                    Box {
                        InstitutionPhotoImage(
                            photo = photo,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        IconButton(
                            onClick = { onRemovePhoto(photo.id) },
                            enabled = !isPhotoProcessing,
                            modifier = Modifier.align(Alignment.TopEnd),
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Удалить фото",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = photoUrlInput,
                onValueChange = onPhotoUrlInputChange,
                label = { Text("Ссылка на фото") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isPhotoProcessing,
            )
            OutlinedButton(
                onClick = onAddPhotoUrl,
                enabled = !isPhotoProcessing && photoUrlInput.isNotBlank(),
            ) {
                Text("URL")
            }
        }

        FilledTonalButton(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            enabled = !isPhotoProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isPhotoProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                Text("Загрузить с устройства", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
