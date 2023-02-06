package com.fireblade.transactionstore.home.domain

enum class TransactionRequest {
    BEGIN,
    COMMIT,
    ROLLBACK
}

enum class TransactionError {
    BEGIN,
    COMMIT,
    ROLLBACK
}
