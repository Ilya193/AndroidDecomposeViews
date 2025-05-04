package ru.ikom.androiddecomposeviews.details

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import ru.ikom.androiddecomposeviews.BaseRootComponent
import ru.ikom.androiddecomposeviews.domain.MessagesRepository
import ru.ikom.androiddecomposeviews.messages.MessagesComponent.State

interface DetailsComponent {
    val states: Flow<State>

    fun onViewCreated()

    fun onBack()

    data class State(
        val text: String,
    ) {
        companion object {
            fun initial(): State =
                State(
                    text = "",
                )
        }
    }
}

class DefaultDetailsComponent(
    componentContext: ComponentContext,
    private val repository: MessagesRepository,
    private val id: Int,
    private val onNavigateBack: () -> Unit,
) : DetailsComponent,
    BaseRootComponent<DetailsComponent.State, DefaultDetailsComponent.Msg,
            DefaultDetailsComponent.Label>(initialState = DetailsComponent.State.initial()),
    ComponentContext by componentContext {

    init {
        getMessage()
    }

    private fun getMessage() {
        val message = repository.get(id) ?: return

        dispatch(Msg.UpdateText(message.message))
    }

    override fun onViewCreated() {
        observerState?.onNext(uiState)
    }

    override fun onBack() {
        onNavigateBack()
    }

    override fun DetailsComponent.State.reduce(msg: Msg): DetailsComponent.State =
        when (msg) {
            is Msg.UpdateText -> copy(text = msg.text)
        }

    sealed interface Msg {
        class UpdateText(val text: String) : Msg
    }

    sealed interface Label
}