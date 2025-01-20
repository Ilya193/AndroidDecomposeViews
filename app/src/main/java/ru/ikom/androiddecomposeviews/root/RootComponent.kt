package ru.ikom.androiddecomposeviews.root

import androidx.lifecycle.Lifecycle
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.ikom.androiddecomposeviews.Observer
import ru.ikom.androiddecomposeviews.counter.CounterComponent
import ru.ikom.androiddecomposeviews.counter.DefaultCounterComponent
import ru.ikom.androiddecomposeviews.details.DefaultDetailsComponent
import ru.ikom.androiddecomposeviews.details.DetailsComponent
import ru.ikom.androiddecomposeviews.observer
import ru.ikom.androiddecomposeviews.to

interface RootComponent {

    val labels: Flow<Label?>

    fun counterComponent(lifecycle: Lifecycle): CounterComponent
    fun detailsComponent(lifecycle: Lifecycle): DetailsComponent

    sealed interface Label {
        class Back : Label
        class OnOpenDetails : Label
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val backCallback = BackCallback { publish(RootComponent.Label.Back()) }

    init {
        componentContext.backHandler.register(backCallback)
    }

    override fun counterComponent(lifecycle: Lifecycle): CounterComponent {
        val counterLifecycle = LifecycleRegistry()

        lifecycle to counterLifecycle

        return DefaultCounterComponent(
            componentContext = childContext(CounterComponent.KEY, counterLifecycle),
            onOpenDetails = { publish(RootComponent.Label.OnOpenDetails()) }
        )
    }

    override fun detailsComponent(lifecycle: Lifecycle): DetailsComponent {
        val detailsLifecycle = LifecycleRegistry()

        lifecycle to detailsLifecycle

        return DefaultDetailsComponent(
            componentContext = childContext(DetailsComponent.KEY, detailsLifecycle),
        )
    }

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