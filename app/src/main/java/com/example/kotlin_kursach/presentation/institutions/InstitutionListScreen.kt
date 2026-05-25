package com.example.kotlin_kursach.presentation.institutions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.toDisplayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionListScreen(
    onInstitutionClick: (String) -> Unit,
    viewModel: InstitutionListViewModel = viewModel(
        factory = InstitutionListViewModelFactory(
            com.example.kotlin_kursach.data.AppContainer.institutionRepository,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Каталог учебных заведений") })
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (val state = uiState) {
                InstitutionListUiState.Loading -> LoadingContent(Modifier.fillMaxSize())
                is InstitutionListUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = viewModel::loadInstitutions,
                    modifier = Modifier.fillMaxSize(),
                )
                is InstitutionListUiState.Success -> {
                    if (state.fromCache) {
                        CacheBanner()
                    }
                    InstitutionListContent(
                        institutions = state.institutions,
                        onInstitutionClick = onInstitutionClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CacheBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = "Офлайн: показаны сохранённые данные",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Повторить")
        }
    }
}

@Composable
private fun InstitutionListContent(
    institutions: List<Institution>,
    onInstitutionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(institutions, key = { it.id }) { institution ->
            InstitutionCard(
                institution = institution,
                onClick = { onInstitutionClick(institution.id) },
            )
        }
    }
}

@Composable
private fun InstitutionCard(
    institution: Institution,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = institution.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${institution.type.toDisplayName()} · ${institution.city}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
