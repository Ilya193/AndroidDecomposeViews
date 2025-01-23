package ru.ikom.androiddecomposeviews.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.ikom.androiddecomposeviews.Observer
import ru.ikom.androiddecomposeviews.observer

interface RootComponent {

    val labels: Flow<Label?>

    sealed interface Label
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private var label: RootComponent.Label? = null
    private var observerLabel: Observer<RootComponent.Label>? = null

    override val labels: Flow<RootComponent.Label?> = callbackFlow {
        sendDataAndReset(channel, label)
        observerLabel = observer { sendDataAndReset(channel, label) }
        awaitClose { observerLabel = null }
    }

    private fun sendDataAndReset(channel: SendChannel<RootComponent.Label?>, label: RootComponent.Label?) {
        channel.trySend(label)
        this.label = null
    }

    private fun publish(label: RootComponent.Label) {
        this.label = label
        observerLabel?.onNext(label)
    }

}