package com.fireblade.transactionstore.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * Internal model state.
 */
interface ModelState

/**
 * Emitted view state for view consumption
 */
interface ViewState

/**
 * Intent representing an action from the user
 */
interface Intent<TModelState : ModelState> {
    fun isValidFor(modelState: TModelState): Boolean = true
}

abstract class MviViewModel<TIntent : Intent<TModelState>,
        TViewState : ViewState,
        TModelState : ModelState>(
    val initialState: TModelState
) : ViewModel() {

    /**
     * Internal model state. In this property, we persist whatever state needs to be persisted in the
     * model but UI doesn't care for.
     */

    private val _modelState = MutableStateFlow(initialState)

    protected val modelState: TModelState
        get() = _modelState.value

    /**
     * Called by the Viewmodel whenever states [modelState] and [viewState] need to get updated.
     * @param stateUpdate a lambda that generates a new [modelState]
     */
    protected fun updateState(stateUpdate: (state: TModelState) -> TModelState) {
        _modelState.value = stateUpdate(modelState)
    }

    /**
     * [viewState] flow always has a value
     */
    val viewState: StateFlow<TViewState>
        get() = _modelState.map {
            reduce(it)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, reduce(initialState))

    /**
     * Method that should be override in every Model created. In this method, base on the latest internal
     * model state, we create a new immutable [viewState]
     * @param state model latest internal state
     */
    protected abstract fun reduce(state: TModelState): TViewState

    /**
     * Called by the UI to feed the model with Intents
     * @param intent the UI originated intent
     */
    fun onIntent(intent: TIntent) {
        viewModelScope.launch {
            if (intent.isValidFor(modelState)) {
                handleIntent(modelState, intent)
            }
        }
    }

    /**
     * Method that should be override in every Model created. In this method we
     * handle the processed intent based on the internal modelState and we decide how to [updateState] or [navigate].
     * @param intent The processed intent
     * @param modelState The latest model internal state
     */
    protected abstract suspend fun handleIntent(modelState: TModelState, intent: TIntent)
}