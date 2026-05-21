package com.example.kotlin_kursach.presentation.institutions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun InstitutionDetailScreen(
    institutionId: String,
    onBack: () -> Unit,
    viewModel: InstitutionDetailViewModel = viewModel(
        factory = InstitutionDetailViewModelFactory(
            repository = com.example.kotlin_kursach.data.AppContainer.institutionRepository,
            institutionId = institutionId,
        ),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Учебное заведение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            InstitutionDetailUiState.Loading -> LoadingContent(Modifier.padding(innerPadding))
            is InstitutionDetailUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = viewModel::loadInstitution,
                modifier = Modifier.padding(innerPadding),
            )
            is InstitutionDetailUiState.Success -> InstitutionDetailContent(
                institution = state.institution,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun InstitutionDetailContent(
    institution: Institution,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = institution.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        DetailRow("Тип", institution.type.toDisplayName())
        DetailRow("Город", institution.city)
        DetailRow("Адрес", institution.address)
        DetailRow("Описание", institution.description)
        institution.phone?.let { DetailRow("Телефон", it) }
        institution.website?.let { DetailRow("Сайт", it) }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp),
    )
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
