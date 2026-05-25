package com.example.kotlin_kursach.core

import com.example.kotlin_kursach.BuildConfig

object AdminConfig {
    private val adminEmails: Set<String> = BuildConfig.ADMIN_EMAILS
        .split(",")
        .map { it.trim().lowercase() }
        .filter { it.isNotEmpty() }
        .toSet()

    fun isAdmin(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return adminEmails.contains(email.trim().lowercase())
    }
}
