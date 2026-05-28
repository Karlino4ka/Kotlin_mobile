package com.example.kotlin_kursach.data.repository

import com.example.kotlin_kursach.data.local.dao.InstitutionDao
import com.example.kotlin_kursach.data.local.entity.InstitutionEntity
import com.example.kotlin_kursach.data.local.toEntity
import com.example.kotlin_kursach.data.remote.InstitutionApi
import com.example.kotlin_kursach.data.remote.InstitutionPhotoApi
import com.example.kotlin_kursach.domain.model.CreateInstitutionInput
import com.example.kotlin_kursach.domain.model.Institution
import com.example.kotlin_kursach.domain.model.InstitutionType
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InstitutionRepositoryImplTest {

    private lateinit var server: MockWebServer
    private lateinit var dao: FakeInstitutionDao
    private lateinit var repository: InstitutionRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        dao = FakeInstitutionDao()
        repository = InstitutionRepositoryImpl(
            api = retrofit.create(InstitutionApi::class.java),
            photoApi = retrofit.create(InstitutionPhotoApi::class.java),
            institutionDao = dao,
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun getInstitutions_savesToCache() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [{
                      "id":"1",
                      "name":"Test Uni",
                      "type":"UNIVERSITY",
                      "city":"Москва",
                      "address":"Addr",
                      "description":"Desc",
                      "reviewCount":1,
                      "averageRating":4.0,
                      "photos":null
                    }]
                    """.trimIndent(),
                ),
        )

        val result = repository.getInstitutions()

        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow().fromCache)
        assertEquals(1, dao.getAll().size)
        assertEquals("Test Uni", result.getOrThrow().value.first().name)
    }

    @Test
    fun getInstitutions_onNetworkError_returnsCache() = runTest {
        dao.upsert(
            Institution(
                id = "cached",
                name = "Cached",
                type = InstitutionType.SCHOOL,
                city = "Минск",
                address = "A",
                description = "D",
                phone = null,
                website = null,
            ).toEntity(),
        )

        server.enqueue(MockResponse().setResponseCode(500))

        val result = repository.getInstitutions()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().fromCache)
        assertEquals("Cached", result.getOrThrow().value.first().name)
    }

    @Test
    fun createInstitution_postsAndCaches() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody(
                    """
                    {
                      "id":"new-1",
                      "name":"Новая школа",
                      "type":"SCHOOL",
                      "city":"Минск",
                      "address":"ул. 1",
                      "description":"Описание"
                    }
                    """.trimIndent(),
                ),
        )

        val result = repository.createInstitution(
            CreateInstitutionInput(
                name = "Новая школа",
                type = InstitutionType.SCHOOL,
                city = "Минск",
                address = "ул. 1",
                description = "Описание",
                phone = null,
                website = null,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals("new-1", result.getOrThrow().id)
        assertEquals("Новая школа", dao.getById("new-1")?.name)
    }
}

private class FakeInstitutionDao : InstitutionDao {
    private val items = mutableListOf<InstitutionEntity>()

    override suspend fun getAll(): List<InstitutionEntity> = items.sortedBy { it.name }

    override suspend fun getById(id: String): InstitutionEntity? = items.find { it.id == id }

    override suspend fun upsertAll(institutions: List<InstitutionEntity>) {
        institutions.forEach { upsert(it) }
    }

    override suspend fun upsert(institution: InstitutionEntity) {
        items.removeAll { it.id == institution.id }
        items.add(institution)
    }

    override suspend fun clearAll() {
        items.clear()
    }

    override suspend fun deleteById(id: String) {
        items.removeAll { it.id == id }
    }
}
