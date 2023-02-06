package com.fireblade.transactionstore.home.presentation

import com.fireblade.transactionstore.home.Intent

sealed class HomeViewIntent : Intent<HomeViewModelState> {
    data class BuildRequest(
        val input: String
    ) : HomeViewIntent()
    data class SubmitRequest(
        val rawRequest: String
    ) : HomeViewIntent()
    object UnknownRequest: HomeViewIntent()
}