package com.example.kotlin_kursach.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    userEmail: String,
    isAdmin: Boolean,
    favoritesCount: Int,
    institutionsCount: Int,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium,
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Каталог учебных заведений",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = "Справочник школ, колледжей и вузов с поиском и офлайн-доступом",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                ListItem(
                    headlineContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Email")
                            if (isAdmin) {
                                AssistChip(
                                    onClick = {},
                                    enabled = false,
                                    label = { Text("Админ") },
                                )
                            }
                        }
                    },
                    supportingContent = { Text(userEmail) },
                    leadingContent = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                )
                if (!isAdmin) {
                    ListItem(
                        headlineContent = { Text("В избранном") },
                        supportingContent = { Text("$favoritesCount заведений") },
                        leadingContent = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        },
                    )
                }
                ListItem(
                    headlineContent = { Text("В каталоге") },
                    supportingContent = { Text("$institutionsCount заведений") },
                    leadingContent = {
                        Icon(Icons.Default.School, contentDescription = null)
                    },
                )
            }
        }
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Text("Выйти", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
