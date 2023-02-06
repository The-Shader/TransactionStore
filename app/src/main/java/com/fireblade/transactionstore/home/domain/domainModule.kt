package com.fireblade.transactionstore.home.domain

import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factory {
        TransactionStoreImpl()
    }.bind(TransactionStoreService::class)
}