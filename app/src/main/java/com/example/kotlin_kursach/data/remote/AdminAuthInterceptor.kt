package com.example.kotlin_kursach.data.remote

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

class AdminAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val request = if (!email.isNullOrBlank()) {
            chain.request().newBuilder()
                .header("X-User-Email", email)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
