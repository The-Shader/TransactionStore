package com.fireblade.transactionstore

import android.app.Application
import com.fireblade.transactionstore.home.applicationModule
import com.fireblade.transactionstore.home.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object KoinStarter {
    @JvmStatic
    fun start(application: Application) {
        stopKoin()
        startKoin {
            androidContext(application)
            modules(
                listOf(
                    applicationModule,
                    domainModule
                )
            )
        }
    }
}