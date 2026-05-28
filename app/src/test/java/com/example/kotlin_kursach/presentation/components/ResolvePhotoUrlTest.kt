package com.example.kotlin_kursach.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResolvePhotoUrlTest {

    @Test
    fun resolvePhotoUrl_rewritesUploadsHostFromApiBase() {
        val resolved = resolvePhotoUrl("http://10.0.2.2:8080/uploads/inst-1/photo.jpg")
        assertTrue(resolved.contains("/uploads/inst-1/photo.jpg"))
        assertEquals(
            com.example.kotlin_kursach.BuildConfig.API_BASE_URL.trimEnd('/') + "/uploads/inst-1/photo.jpg",
            resolved,
        )
    }

    @Test
    fun resolvePhotoUrl_keepsExternalHttpsUrl() {
        val url = "https://cdn.example.com/image.png"
        assertEquals(url, resolvePhotoUrl(url))
    }

    @Test
    fun resolvePhotoUrl_keepsContentUri() {
        val uri = "content://media/external/images/1"
        assertEquals(uri, resolvePhotoUrl(uri))
    }
}
