package ru.ikom.androiddecomposeviews.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent {
    val state: StateFlow<State>

    data class State(
        val text: String,
    ) {
        companion object {
            fun initial(): State =
                State(
                    text = "init",
                )
        }
    }

    companion object {
        const val KEY = "DetailsComponent"
    }
}

class DefaultDetailsComponent(
    componentContext: ComponentContext,
) : DetailsComponent, ComponentContext by componentContext {

    override val state = MutableStateFlow(DetailsComponent.State.initial())

    init {
        componentContext.lifecycle.doOnCreate {
            println("s149 doOnCreate DETAILS")
        }
        componentContext.lifecycle.doOnDestroy {
            println("s149 doOnDestroy DETAILS")
        }
    }
}