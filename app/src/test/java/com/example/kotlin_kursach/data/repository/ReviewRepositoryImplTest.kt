package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.remote.ReviewApi
import com.example.kotlin_kursach.domain.model.SubmitReviewInput
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewRepositoryImplTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: ReviewRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReviewApi::class.java)
        repository = ReviewRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getReviews_returnsList() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [{
                      "id":"r1",
                      "institutionId":"1",
                      "userId":"u1",
                      "authorEmail":"a@test.com",
                      "rating":5,
                      "text":"Хорошо",
                      "createdAt":"2026-05-26"
                    }]
                    """.trimIndent(),
                ),
        )

        val result = repository.getReviews("1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals(5, result.getOrThrow().first().rating)
    }

    @Test
    fun submitReview_returnsCreatedReview() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(
                    """
                    {
                      "id":"r2",
                      "institutionId":"1",
                      "userId":"u2",
                      "authorEmail":"b@test.com",
                      "rating":4,
                      "text":"Норм",
                      "createdAt":"2026-05-26"
                    }
                    """.trimIndent(),
                ),
        )

        val result = repository.submitReview(
            institutionId = "1",
            input = SubmitReviewInput(rating = 4, text = "Норм"),
        )

        assertTrue(result.isSuccess)
        assertEquals("r2", result.getOrThrow().id)
    }
}
