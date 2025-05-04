@file:OptIn(ExperimentalDecomposeApi::class)

package ru.ikom.androiddecomposeviews.messages

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.android.ViewContext
import com.arkivanov.decompose.extensions.android.layoutInflater
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.BaseView
import ru.ikom.androiddecomposeviews.R
import ru.ikom.androiddecomposeviews.childCoroutineScope
import ru.ikom.androiddecomposeviews.diff
import ru.ikom.androiddecomposeviews.messages.model.MessageUi

interface MessagesView : BaseView<MessagesView.Model> {

    data class Model(
        val messages: List<MessageUi>,
    )
}

val stateToModel: MessagesComponent.State.() -> MessagesView.Model =
    {
        MessagesView.Model(
            messages = messages,
        )
    }

fun ViewContext.messagesView(
    component: MessagesComponent
): View {
    val adapter = MessagesAdapter(onClick = component::onClickMessage)

    val layout = layoutInflater.inflate(R.layout.messages_screen, parent, false)
    val messages: RecyclerView = layout.findViewById(R.id.messages)

    messages.adapter = adapter

    val scope = childCoroutineScope(lifecycle)

    val viewRenderer =
        diff {
            diff(
                get = MessagesView.Model::messages,
                compare = { a, b -> a === b },
                set = adapter::submitList
            )
        }

    scope.launch {
        component.states.map(stateToModel).collect {
            viewRenderer.render(it)
        }
    }

    component.onViewCreated()

    return layout
}