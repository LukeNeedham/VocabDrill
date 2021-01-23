@file:Suppress("unused")

package com.lukeneedham.vocabdrill

import android.app.Application
import com.lukeneedham.vocabdrill.di.KoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(KoinModule.modules)
        }
    }
}
