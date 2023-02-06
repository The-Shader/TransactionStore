package com.fireblade.transactionstore.home.domain

import arrow.core.Either

interface TransactionStoreService {
    fun processTransactionRequest(request: TransactionRequest): Either<TransactionError, Unit>
    fun processPropertyRequest(request: PropertyRequest): Either<PropertyError, PropertyResponse>
    fun printStore()
}