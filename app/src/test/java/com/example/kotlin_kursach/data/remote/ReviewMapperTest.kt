package com.example.kotlin_kursach.data.remote

import com.example.kotlin_kursach.data.remote.dto.ReviewDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewMapperTest {

    @Test
    fun toDomain_mapsReviewFields() {
        val dto = ReviewDto(
            id = "r1",
            institutionId = "1",
            userId = "u1",
            authorEmail = "user@test.com",
            authorName = "Иван",
            rating = 5,
            text = "Отлично",
            createdAt = "2026-05-26T12:00:00",
        )

        val domain = dto.toDomain()

        assertEquals("r1", domain.id)
        assertEquals(5, domain.rating)
        assertEquals("Отлично", domain.text)
        assertEquals("user@test.com", domain.authorEmail)
    }
}
