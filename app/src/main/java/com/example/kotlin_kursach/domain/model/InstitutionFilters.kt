package com.example.kotlin_kursach.domain.model

enum class InstitutionSortOrder {
    NAME_ASC,
    CITY_ASC,
    TYPE,
}

data class InstitutionFilters(
    val searchQuery: String = "",
    val type: InstitutionType? = null,
    val city: String? = null,
    val sortOrder: InstitutionSortOrder = InstitutionSortOrder.NAME_ASC,
)

fun List<Institution>.applyFilters(filters: InstitutionFilters): List<Institution> {
    val query = filters.searchQuery.trim().lowercase()
    return this
        .filter { institution ->
            if (query.isEmpty()) return@filter true
            institution.name.lowercase().contains(query) ||
                institution.city.lowercase().contains(query) ||
                institution.address.lowercase().contains(query) ||
                institution.description.lowercase().contains(query)
        }
        .filter { institution ->
            filters.type == null || institution.type == filters.type
        }
        .filter { institution ->
            filters.city == null || institution.city == filters.city
        }
        .let { list ->
            when (filters.sortOrder) {
                InstitutionSortOrder.NAME_ASC -> list.sortedBy { it.name }
                InstitutionSortOrder.CITY_ASC -> list.sortedBy { it.city }
                InstitutionSortOrder.TYPE -> list.sortedBy { it.type.ordinal }
            }
        }
}
