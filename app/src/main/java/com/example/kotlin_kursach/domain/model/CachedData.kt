package com.example.kotlin_kursach.domain.model

/**
 * @param fromCache true — данные взяты из Room (офлайн или сеть недоступна).
 */
data class CachedData<T>(
    val value: T,
    val fromCache: Boolean,
)
