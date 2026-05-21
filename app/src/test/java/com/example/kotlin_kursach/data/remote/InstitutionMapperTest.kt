package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.InstitutionDto
import com.example.kotlin_kursach.data.remote.dto.InstitutionTypeDto
import com.example.kotlin_kursach.domain.model.InstitutionType
import org.junit.Assert.assertEquals
import org.junit.Test

class InstitutionMapperTest {

    @Test
    fun toDomain_mapsAllFields() {
        val dto = InstitutionDto(
            id = "1",
            name = "Test University",
            type = InstitutionTypeDto.UNIVERSITY,
            city = "Moscow",
            address = "Street 1",
            description = "Description",
            phone = "+7 000",
            website = "https://example.com",
        )

        val domain = dto.toDomain()

        assertEquals("1", domain.id)
        assertEquals("Test University", domain.name)
        assertEquals(InstitutionType.UNIVERSITY, domain.type)
        assertEquals("Moscow", domain.city)
        assertEquals("+7 000", domain.phone)
    }
}
