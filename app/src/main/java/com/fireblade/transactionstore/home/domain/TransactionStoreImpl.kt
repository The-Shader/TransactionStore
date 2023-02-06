package com.fireblade.transactionstore.home.domain

import arrow.core.Either

class TransactionStoreImpl : TransactionStoreService {
    private var currentTx = Transaction(
        store = mutableMapOf()
    )

    override fun processTransactionRequest(request: TransactionRequest): Either<TransactionError, Unit> {
        return when (request) {
            TransactionRequest.BEGIN -> insertNewTransaction()
            TransactionRequest.COMMIT -> commitCurrentTransaction()
            TransactionRequest.ROLLBACK -> rollbackCurrentTransaction()
        }
    }

    override fun processPropertyRequest(request: PropertyRequest): Either<PropertyError, PropertyResponse> {
        return when (request) {
            is PropertyRequest.Count -> countValues(request.value)
            is PropertyRequest.Delete -> delete(
                key = request.name
            )
            is PropertyRequest.Get -> getValue(
                key = request.name
            )
            is PropertyRequest.Set -> setValue(
                key = request.name,
                value = request.value
            )
        }
    }


    private fun insertNewTransaction(): Either<TransactionError, Unit> {
        return Either.catch(
            f = {
                currentTx.nestedTx = Transaction(
                    store = mutableMapOf(),
                    parentTx = currentTx
                )
                currentTx = currentTx.nestedTx!!
                currentTx.parentTx?.store?.let { environment ->
                    currentTx.store.putAll(environment)
                }
            },
            fe = {
                TransactionError.BEGIN
            }
        )
    }

    private fun commitCurrentTransaction(): Either<TransactionError, Unit> {
        return Either.catch(
            f = {
                currentTx.parentTx?.let { parentTx ->
                    currentTx = parentTx.copy(
                        store = currentTx.store,
                        nestedTx = null
                    )
                } ?: throw IllegalStateException("Could not commit")
            },
            fe = {
                TransactionError.COMMIT
            }
        )
    }

    private fun rollbackCurrentTransaction(): Either<TransactionError, Unit> {
        return Either.catch(
            f = {
                currentTx.parentTx?.let { parentTx ->
                    currentTx = parentTx.copy(
                        nestedTx = null
                    )
                } ?: throw IllegalStateException("Could not rollback")
            },
            fe = {
                TransactionError.ROLLBACK
            }
        )
    }

    private fun countValues(value: String): Either<PropertyError, PropertyResponse> {
        return Either.catch(
            f = {
                PropertyResponse.Result(
                    value = currentTx.store.values.count { it == value }.toString()
                )
            },
            fe = {
                PropertyError.COUNT
            }
        )
    }

    private fun setValue(key: String, value: String): Either<PropertyError, PropertyResponse> {
        return Either.catch(
            f = {
                currentTx.store[key] = value
                PropertyResponse.Empty
            },
            fe = {
                PropertyError.SET
            }
        )
    }

    private fun getValue(key: String): Either<PropertyError, PropertyResponse> {
        return Either.catch(
            f = {
                PropertyResponse.Result(
                    value = currentTx.store[key] ?: throw IllegalStateException("Value not found")
                )

            },
            fe = {
                PropertyError.GET
            }
        )
    }

    private fun delete(key: String): Either<PropertyError, PropertyResponse> {
        return Either.catch(
            f = {
                currentTx.store.remove(key)
                PropertyResponse.Empty
            },
            fe = {
                PropertyError.DELETE
            }
        )
    }

    fun printStore() {
        currentTx.store.map {
            print("${it.key} - ${it.value} ; ")
        }
    }
}