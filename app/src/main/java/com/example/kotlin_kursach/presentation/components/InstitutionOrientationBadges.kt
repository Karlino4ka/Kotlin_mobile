package com.example.kotlin_kursach.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kotlin_kursach.domain.model.InstitutionOrientation
import com.example.kotlin_kursach.domain.model.toDisplayName
import com.example.kotlin_kursach.ui.theme.CollegeColor
import com.example.kotlin_kursach.ui.theme.EduTeal
import com.example.kotlin_kursach.ui.theme.UniversityColor

private fun InstitutionOrientation.accentColor(): Color = when (this) {
    InstitutionOrientation.TECHNICAL -> UniversityColor
    InstitutionOrientation.HUMANITARIAN -> CollegeColor
    InstitutionOrientation.MEDICAL -> EduTeal
}

@Composable
fun InstitutionOrientationBadges(
    orientations: List<InstitutionOrientation>,
    modifier: Modifier = Modifier,
) {
    if (orientations.isEmpty()) return
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        orientations.forEach { orientation ->
            val color = orientation.accentColor()
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text(orientation.toDisplayName()) },
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = color.copy(alpha = 0.12f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}
