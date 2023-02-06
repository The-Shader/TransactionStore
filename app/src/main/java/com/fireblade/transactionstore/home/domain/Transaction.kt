package com.fireblade.transactionstore.home.domain

data class Transaction(
    val store: Map<String, String>,
    val nestedTx: Transaction? = null,
    val parentTx: Transaction? = null
)
