package com.fireblade.transactionstore.home.presentation

import com.fireblade.transactionstore.home.ModelState

data class HomeViewModelState(
    val request: String = "",
    val result: String = "",
    val errorStatus: ErrorStatus = ErrorStatus.NONE
) : ModelState

enum class ErrorStatus {
    NONE,
    INVALID_REQUEST,
    GET,
    DELETE,
    COMMIT,
    ROLLBACK,
    GENERAL
}
