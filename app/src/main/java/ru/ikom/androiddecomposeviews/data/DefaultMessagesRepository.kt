package ru.ikom.androiddecomposeviews.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.ikom.androiddecomposeviews.domain.MessageDomain
import ru.ikom.androiddecomposeviews.domain.MessagesRepository

class DefaultMessagesRepository : MessagesRepository {

    private val _messages =
        MutableStateFlow(
            (0..100).map {
                MessageDomain(id = it, "message $it")
            }
        )

    override fun messages(): Flow<List<MessageDomain>> = _messages

    override fun get(id: Int): MessageDomain? =
        _messages.value.firstOrNull { it.id == id }
}