package com.example.kotlin_kursach.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InstitutionFiltersTest {

    private val sample = listOf(
        Institution(
            id = "1",
            name = "МГУ",
            type = InstitutionType.UNIVERSITY,
            orientations = listOf(InstitutionOrientation.HUMANITARIAN, InstitutionOrientation.TECHNICAL),
            city = "Москва",
            address = "Ленинские горы",
            description = "Классический университет",
            phone = null,
            website = null,
        ),
        Institution(
            id = "2",
            name = "Казанский колледж IT",
            type = InstitutionType.COLLEGE,
            orientations = listOf(InstitutionOrientation.TECHNICAL),
            city = "Казань",
            address = "Баумана",
            description = "IT колледж",
            phone = null,
            website = null,
        ),
        Institution(
            id = "3",
            name = "Школа №1",
            type = InstitutionType.SCHOOL,
            orientations = listOf(InstitutionOrientation.MEDICAL),
            city = "Минск",
            address = "Центр",
            description = "Средняя школа",
            phone = null,
            website = null,
        ),
    )

    @Test
    fun applyFilters_searchByCity() {
        val result = sample.applyFilters(
            InstitutionFilters(searchQuery = "казань"),
        )
        assertEquals(1, result.size)
        assertEquals("2", result.first().id)
    }

    @Test
    fun applyFilters_filterByType() {
        val result = sample.applyFilters(
            InstitutionFilters(type = InstitutionType.SCHOOL),
        )
        assertEquals(1, result.size)
        assertEquals(InstitutionType.SCHOOL, result.first().type)
    }

    @Test
    fun applyFilters_sortByName() {
        val result = sample.applyFilters(
            InstitutionFilters(sortOrder = InstitutionSortOrder.NAME_ASC),
        )
        assertEquals(
            listOf("Казанский колледж IT", "МГУ", "Школа №1"),
            result.map { it.name },
        )
    }

    @Test
    fun applyFilters_filterByOrientation() {
        val result = sample.applyFilters(
            InstitutionFilters(orientation = InstitutionOrientation.TECHNICAL),
        )
        assertEquals(2, result.size)
        assertTrue(result.all { InstitutionOrientation.TECHNICAL in it.orientations })
    }

    @Test
    fun applyFilters_sortByCity() {
        val result = sample.applyFilters(
            InstitutionFilters(sortOrder = InstitutionSortOrder.CITY_ASC),
        )
        assertTrue(result.first().city <= result.last().city)
    }
}
