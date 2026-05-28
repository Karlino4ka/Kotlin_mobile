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

    @Test
    fun toDomain_nullPhotos_becomesEmptyList() {
        val dto = InstitutionDto(
            id = "2",
            name = "School",
            type = InstitutionTypeDto.SCHOOL,
            city = "Минск",
            address = "Addr",
            description = "Desc",
            photos = null,
            reviewCount = 3,
            averageRating = 4.5,
        )

        val domain = dto.toDomain()

        assertEquals(0, domain.photos.size)
        assertEquals(3, domain.reviewCount)
        assertEquals(4.5, domain.averageRating!!, 0.01)
    }

    @Test
    fun toDomain_mapsPhotos() {
        val dto = InstitutionDto(
            id = "3",
            name = "College",
            type = InstitutionTypeDto.COLLEGE,
            city = "Казань",
            address = "Addr",
            description = "Desc",
            photos = listOf(
                com.example.kotlin_kursach.data.remote.dto.InstitutionPhotoDto(
                    id = "p1",
                    url = "http://10.0.2.2:8080/uploads/3/a.jpg",
                ),
            ),
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.photos.size)
        assertEquals("p1", domain.photos.first().id)
    }
}
