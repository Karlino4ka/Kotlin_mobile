package com.example.kotlin_kursach.data.local

import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionType
import org.junit.Assert.assertEquals
import org.junit.Test

class InstitutionEntityMapperTest {

    @Test
    fun entityAndDomain_roundTrip() {
        val institution = Institution(
            id = "1",
            name = "Test",
            type = InstitutionType.COLLEGE,
            city = "Kazan",
            address = "Addr",
            description = "Desc",
            phone = null,
            website = "https://test.com",
            averageRating = 4.2,
            reviewCount = 2,
            photos = listOf(
                com.example.kotlin_kursach.domain.model.InstitutionPhoto(
                    id = "p1",
                    url = "http://example.com/1.jpg",
                ),
            ),
        )

        val entity = institution.toEntity()
        val restored = entity.toDomain()

        assertEquals(institution, restored)
    }
}
