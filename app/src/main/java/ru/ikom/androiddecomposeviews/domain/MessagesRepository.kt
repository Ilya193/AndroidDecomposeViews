package ru.ikom.androiddecomposeviews.domain

import kotlinx.coroutines.flow.Flow

interface MessagesRepository {

    fun messages(): Flow<List<MessageDomain>>

    fun get(id: Int): MessageDomain?
}

data class MessageDomain(
    val id: Int,
    val message: String,
)