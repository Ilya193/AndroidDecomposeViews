package ru.ikom.androiddecomposeviews.messages

import ru.ikom.androiddecomposeviews.domain.MessageDomain
import ru.ikom.androiddecomposeviews.messages.model.MessageUi

fun createMessageUi(data: MessageDomain): MessageUi = with(data) {
    MessageUi(
        id = id,
        message = message,
    )
}