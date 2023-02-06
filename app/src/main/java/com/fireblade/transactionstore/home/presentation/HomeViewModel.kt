package com.fireblade.transactionstore.home.presentation

import com.fireblade.transactionstore.home.MviViewModel
import com.fireblade.transactionstore.home.domain.*

class HomeViewModel(
    private val transactionStoreService: TransactionStoreService
) : MviViewModel<HomeViewIntent, HomeViewState, HomeViewModelState>(initialState = HomeViewModelState()) {
    override fun reduce(state: HomeViewModelState): HomeViewState {
        return HomeViewState(
            request = state.request,
            result = state.result,
            errorStatus = state.errorStatus
        )
    }

    override suspend fun handleIntent(modelState: HomeViewModelState, intent: HomeViewIntent) {
        when (intent) {
            is HomeViewIntent.BuildRequest -> updateState {
                modelState.copy(
                    request = intent.input,
                    result = "",
                    errorStatus = ErrorStatus.NONE
                )
            }
            is HomeViewIntent.SubmitRequest -> processRequest(intent.rawRequest)
            HomeViewIntent.UnknownRequest -> updateState {
                modelState.copy(
                    result = "",
                    errorStatus = ErrorStatus.INVALID_REQUEST
                )
            }
        }
    }

    private fun processRequest(rawRequest: String) {
        val arguments = rawRequest.split(" ")
        when {
            arguments.size == 1 -> processTransactionRequest(arguments.first())
            arguments.size > 1 -> processInstruction(arguments.first(), arguments.drop(1))
            else -> updateState {
                modelState.copy(
                    result = "",
                    errorStatus = ErrorStatus.INVALID_REQUEST
                )
            }
        }
    }

    private fun processTransactionRequest(request: String) {
        try {
            updateState {
                when (TransactionRequestType.valueOf(request)) {
                    TransactionRequestType.BEGIN -> transactionStoreService.processTransactionRequest(
                        request = TransactionRequest.BEGIN
                    )
                    TransactionRequestType.COMMIT -> transactionStoreService.processTransactionRequest(
                        request = TransactionRequest.COMMIT
                    )
                    TransactionRequestType.ROLLBACK -> transactionStoreService.processTransactionRequest(
                        request = TransactionRequest.ROLLBACK
                    )
                }.fold(
                    ifLeft = {
                        modelState.copy(
                            result = "",
                            errorStatus = when (it) {
                                TransactionError.BEGIN -> ErrorStatus.GENERAL
                                TransactionError.COMMIT -> ErrorStatus.COMMIT
                                TransactionError.ROLLBACK -> ErrorStatus.ROLLBACK
                            }
                        )
                    },
                    ifRight = {
                        modelState.copy(
                            result = "",
                            errorStatus = ErrorStatus.NONE
                        )
                    }
                )
            }
        } catch (ex: IllegalArgumentException) {
            updateState {
                modelState.copy(
                    result = "",
                    errorStatus = ErrorStatus.INVALID_REQUEST
                )
            }
        }

    }

    private fun processInstruction(request: String, arguments: List<String>) {
        try {
            updateState {
                when (PropertyRequestType.valueOf(request)) {
                    PropertyRequestType.GET -> transactionStoreService.processPropertyRequest(
                        request = PropertyRequest.Get(
                            name = arguments.first()
                        )
                    )
                    PropertyRequestType.SET -> {
                        if (arguments.size != 2) {
                            throw IllegalArgumentException()
                        }
                        transactionStoreService.processPropertyRequest(
                            request = PropertyRequest.Set(
                                name = arguments.first(),
                                value = arguments.last()
                            )
                        )
                    }
                    PropertyRequestType.COUNT -> transactionStoreService.processPropertyRequest(
                        request = PropertyRequest.Count(
                            value = arguments.first()
                        )
                    )
                    PropertyRequestType.DELETE -> transactionStoreService.processPropertyRequest(
                        request = PropertyRequest.Delete(
                            name = arguments.first()
                        )
                    )
                }.fold(
                    ifLeft = {
                        modelState.copy(
                            result = "",
                            errorStatus = when (it) {
                                PropertyError.GET -> ErrorStatus.GET
                                PropertyError.SET,
                                PropertyError.COUNT -> ErrorStatus.GENERAL
                                PropertyError.DELETE -> ErrorStatus.DELETE
                            }
                        )
                    },
                    ifRight = { result ->
                        when (result) {
                            is PropertyResponse.Result -> modelState.copy(
                                result = result.value,
                                errorStatus = ErrorStatus.NONE
                            )
                            PropertyResponse.Empty -> modelState.copy(
                                result = "",
                                errorStatus = ErrorStatus.NONE
                            )
                        }
                    }
                )
            }
        } catch (ex: IllegalArgumentException) {
            updateState {
                modelState.copy(
                    result = "",
                    errorStatus = ErrorStatus.INVALID_REQUEST
                )
            }
        }
    }
}

enum class TransactionRequestType {
    BEGIN,
    COMMIT,
    ROLLBACK
}

enum class PropertyRequestType {
    GET,
    SET,
    COUNT,
    DELETE
}