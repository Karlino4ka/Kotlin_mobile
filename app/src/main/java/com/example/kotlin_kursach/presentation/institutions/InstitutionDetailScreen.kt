package com.example.kotlin_kursach.presentation.institutions

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.toDisplayName
import com.example.kotlin_kursach.presentation.components.InstitutionTypeBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionDetailScreen(
    onBack: () -> Unit,
    isAdmin: Boolean = false,
    viewModel: InstitutionDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavoriteState.collectAsStateWithLifecycle()
    val showFavorite = !isAdmin
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Учебное заведение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (showFavorite) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Избранное",
                                tint = if (isFavorite) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            viewModel.shareText()?.let { text ->
                                context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        },
                                        "Поделиться",
                                    ),
                                )
                            }
                        },
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            InstitutionDetailUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            is InstitutionDetailUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::loadInstitution,
                modifier = Modifier.padding(innerPadding),
            )
            is InstitutionDetailUiState.Success -> Column(Modifier.padding(innerPadding)) {
                if (state.fromCache) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            "Офлайн: сохранённые данные",
                            modifier = Modifier.padding(16.dp, 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                InstitutionDetailContent(
                    institution = state.institution,
                    onCall = { phone ->
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                    },
                    onOpenWebsite = { url ->
                        val normalized = if (url.startsWith("http")) url else "https://$url"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
                    },
                )
            }
        }
    }
}

@Composable
private fun InstitutionDetailContent(
    institution: Institution,
    onCall: (String) -> Unit,
    onOpenWebsite: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = institution.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        InstitutionTypeBadge(type = institution.type)
        InfoCard(title = "Город", value = institution.city)
        InfoCard(title = "Адрес", value = institution.address)
        InfoCard(title = "Описание", value = institution.description)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            institution.phone?.let { phone ->
                FilledTonalButton(
                    onClick = { onCall(phone) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text("Позвонить", modifier = Modifier.padding(start = 6.dp))
                }
            }
            institution.website?.let { site ->
                OutlinedButton(
                    onClick = { onOpenWebsite(site) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Language, contentDescription = null)
                    Text("Сайт", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
        Text(
            text = institution.type.toDisplayName(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Повторить") }
    }
}
