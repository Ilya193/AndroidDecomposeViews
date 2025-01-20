package ru.ikom.androiddecomposeviews.counter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CounterComponent {
    val state: StateFlow<State>

    fun openDetails()

    data class State(
        val counter: Int,
    ) {
        companion object {
            fun initial(): State =
                State(
                    counter = 0,
                )
        }
    }

    companion object {
        const val KEY = "CounterComponent"
    }
}

class DefaultCounterComponent(
    componentContext: ComponentContext,
    private val onOpenDetails: () -> Unit,
) : CounterComponent, ComponentContext by componentContext {

    private val scope =
        componentContext.coroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override val state = MutableStateFlow(CounterComponent.State.initial())

    init {
        scope.launch {
            while (true) {
                delay(1000)
                state.update { it.copy(counter = it.counter + 1) }
            }
        }

        componentContext.lifecycle.doOnStart {
            println("s149 doOnStart")
        }

        componentContext.lifecycle.doOnResume {
            println("s149 doOnResume")
        }

        componentContext.lifecycle.doOnPause {
            println("s149 doOnPause")
        }

        componentContext.lifecycle.doOnStop {
            println("s149 doOnStop")
        }
    }

    override fun openDetails() {
        onOpenDetails()
    }
}