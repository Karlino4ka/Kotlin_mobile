package com.example.kotlin_kursach

import android.app.Application
import com.example.kotlin_kursach.data.AppContainer

class KotlinKursachApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
