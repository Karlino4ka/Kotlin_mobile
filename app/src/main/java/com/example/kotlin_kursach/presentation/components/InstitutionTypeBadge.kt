package com.example.kotlin_kursach.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.kotlin_kursach.domain.model.InstitutionType
import com.example.kotlin_kursach.domain.model.toDisplayName
import com.example.kotlin_kursach.ui.theme.CollegeColor
import com.example.kotlin_kursach.ui.theme.SchoolColor
import com.example.kotlin_kursach.ui.theme.UniversityColor

private fun InstitutionType.icon(): ImageVector = when (this) {
    InstitutionType.SCHOOL -> Icons.Default.School
    InstitutionType.COLLEGE -> Icons.Default.MenuBook
    InstitutionType.UNIVERSITY -> Icons.Default.AccountBalance
}

private fun InstitutionType.accentColor(): Color = when (this) {
    InstitutionType.SCHOOL -> SchoolColor
    InstitutionType.COLLEGE -> CollegeColor
    InstitutionType.UNIVERSITY -> UniversityColor
}

@Composable
fun InstitutionTypeBadge(
    type: InstitutionType,
    modifier: Modifier = Modifier,
) {
    val color = type.accentColor()
    AssistChip(
        onClick = {},
        modifier = modifier,
        enabled = false,
        label = { Text(type.toDisplayName()) },
        leadingIcon = {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = color,
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = color.copy(alpha = 0.12f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}
