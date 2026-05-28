package com.example.kotlin_kursach.data.remote

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response

class AdminAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser
        val requestBuilder = chain.request().newBuilder()
        if (user != null) {
            requestBuilder.header("X-User-Id", user.uid)
            user.email?.takeIf { it.isNotBlank() }?.let { email ->
                requestBuilder.header("X-User-Email", email)
            }
            user.displayName?.takeIf { it.isNotBlank() }?.let { name ->
                requestBuilder.header("X-User-Name", name)
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}
