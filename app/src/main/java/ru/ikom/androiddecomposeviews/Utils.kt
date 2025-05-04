package ru.ikom.androiddecomposeviews

import androidx.lifecycle.ViewModel
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.receiveAsFlow

interface Observer<T> {
    fun onNext(value: T)
}

inline fun <T> observer(crossinline onNext: (T) -> Unit): Observer<T> = object : Observer<T> {
    override fun onNext(value: T) {
        onNext(value)
    }

}

fun childCoroutineScope() = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
fun childCoroutineScope(lifecycle: Lifecycle): CoroutineScope {
    val scope = childCoroutineScope()

    lifecycle.doOnDestroy(scope::cancel)

    return scope
}

abstract class BaseRootComponent<State : Any, Msg : Any, Label: Any>(
    private val initialState: State,
) {

    protected var uiState = initialState

    protected var observerState: Observer<State>? = null

    val states: Flow<State> = callbackFlow {
        observerState = observer(channel::trySend)
        awaitClose { observerState = null }
    }

    private val _labels = Channel<Label>(capacity = Channel.BUFFERED)
    val labels = _labels.receiveAsFlow()

    protected fun dispatch(msg: Msg) {
        uiState = uiState.reduce(msg)
        observerState?.onNext(uiState)
    }

    protected inline fun dispatch(block: State.() -> State) {
        uiState = block(uiState)
        observerState?.onNext(uiState)
    }

    protected fun publish(label: Label) {
        _labels.trySend(label)
    }

    protected abstract fun State.reduce(msg: Msg): State
}