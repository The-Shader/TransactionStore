package com.fireblade.transactionstore.home

import com.fireblade.transactionstore.home.presentation.HomeViewModel
import org.koin.dsl.module

val applicationModule = module {
    factory {
        HomeViewModel(
            transactionStoreService = get()
        )
    }
}