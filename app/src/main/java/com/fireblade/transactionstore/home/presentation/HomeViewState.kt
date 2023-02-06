package com.fireblade.transactionstore.home.presentation

import com.fireblade.transactionstore.home.ViewState

data class HomeViewState(
    val request: String,
    val result: String,
    val errorStatus: ErrorStatus
) : ViewState