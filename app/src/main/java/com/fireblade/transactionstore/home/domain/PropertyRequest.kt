package com.fireblade.transactionstore.home.domain

sealed class PropertyRequest {
    data class Get(val name: String) : PropertyRequest()
    data class Set(val name: String, val value: String) : PropertyRequest()
    data class Count(val value: String) : PropertyRequest()
    data class Delete(val name: String) : PropertyRequest()
}