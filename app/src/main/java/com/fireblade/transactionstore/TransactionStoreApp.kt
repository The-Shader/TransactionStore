package com.fireblade.transactionstore

import android.app.Application

class TransactionStoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinStarter.start(this)
    }
}