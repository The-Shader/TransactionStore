package com.fireblade.transactionstore.home.domain

data class Transaction(
    val store: MutableMap<String, String>,
    var nestedTx: Transaction? = null,
    var parentTx: Transaction? = null
)
