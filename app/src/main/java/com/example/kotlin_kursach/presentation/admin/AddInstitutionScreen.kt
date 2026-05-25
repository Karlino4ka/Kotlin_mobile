package com.example.kotlin_kursach.presentation.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kotlin_kursach.domain.model.InstitutionType
import com.example.kotlin_kursach.domain.model.toDisplayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstitutionScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: AddInstitutionViewModel = hiltViewModel(),
) {
    val form by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(form.isSuccess) {
        if (form.isSuccess) {
            onCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить заведение") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Новая запись появится в каталоге для всех пользователей",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = form.name,
                onValueChange = viewModel::updateName,
                label = { Text("Название *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Text("Тип *", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InstitutionType.entries.forEach { type ->
                    FilterChip(
                        selected = form.type == type,
                        onClick = { viewModel.updateType(type) },
                        label = { Text(type.toDisplayName()) },
                    )
                }
            }
            OutlinedTextField(
                value = form.city,
                onValueChange = viewModel::updateCity,
                label = { Text("Город *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = form.address,
                onValueChange = viewModel::updateAddress,
                label = { Text("Адрес *") },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = form.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Описание *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )
            OutlinedTextField(
                value = form.phone,
                onValueChange = viewModel::updatePhone,
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = form.website,
                onValueChange = viewModel::updateWebsite,
                label = { Text("Сайт") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            form.errorMessage?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = viewModel::submit,
                enabled = form.canSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (form.isSubmitting) {
                    CircularProgressIndicator()
                } else {
                    Text("Сохранить")
                }
            }
        }
    }
}
