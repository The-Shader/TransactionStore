package com.fireblade.transactionstore.home.domain

sealed class PropertyResponse {
    data class Result(val value: String) : PropertyResponse()
    object Empty : PropertyResponse()
}

enum class PropertyError {
    GET,
    SET,
    COUNT,
    DELETE
}