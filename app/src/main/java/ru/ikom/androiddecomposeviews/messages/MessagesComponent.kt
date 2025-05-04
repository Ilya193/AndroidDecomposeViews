package ru.ikom.androiddecomposeviews.messages

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.BaseRootComponent
import ru.ikom.androiddecomposeviews.domain.MessagesRepository
import ru.ikom.androiddecomposeviews.messages.model.MessageUi

interface MessagesComponent {
    val states: Flow<State>

    fun onViewCreated()

    fun onClickMessage(position: Int)

    data class State(
        val messages: List<MessageUi>,
    ) {
        companion object {
            fun initial(): State =
                State(
                    messages = emptyList(),
                )
        }
    }
}

class DefaultMessagesComponent(
    componentContext: ComponentContext,
    private val repository: MessagesRepository,
    private val onOpenDetails: (Int) -> Unit,
) : MessagesComponent,
    BaseRootComponent<MessagesComponent.State, DefaultMessagesComponent.Msg,
            DefaultMessagesComponent.Label>(initialState = MessagesComponent.State.initial()),
    ComponentContext by componentContext {

    private val scope =
        componentContext.coroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    init {
        scope.launch {
            repository.messages().collect {
                val messagesUi = it.map(::createMessageUi)
                dispatch(Msg.UpdateMessages(messagesUi))
            }
        }
    }

    override fun onViewCreated() {
        observerState?.onNext(uiState)
    }

    override fun onClickMessage(position: Int) {
        val state = uiState
        val messages = state.messages
        val item = messages[position]

        onOpenDetails(item.id)
    }

    override fun MessagesComponent.State.reduce(msg: Msg): MessagesComponent.State =
        when (msg) {
            is Msg.UpdateMessages -> copy(messages = msg.messages)
        }

    sealed interface Msg {
        class UpdateMessages(val messages: List<MessageUi>) : Msg
    }

    sealed interface Label
}