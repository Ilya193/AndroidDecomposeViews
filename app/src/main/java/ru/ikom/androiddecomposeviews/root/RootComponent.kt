package ru.ikom.androiddecomposeviews.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ComponentContextFactory
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.ikom.androiddecomposeviews.Observer
import ru.ikom.androiddecomposeviews.counter.CounterComponent
import ru.ikom.androiddecomposeviews.counter.DefaultCounterComponent
import ru.ikom.androiddecomposeviews.observer

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface RootComponent {

    val labels: Flow<Label?>

    fun counterComponent(lifecycle: androidx.lifecycle.Lifecycle): CounterComponent

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

    override fun counterComponent(lifecycle: androidx.lifecycle.Lifecycle): CounterComponent {
        var ttt: ComponentContext? = null
        val newComponentContext = ComponentContextFactory { lifecycle, stateKeeper, instanceKeeper, backHandler ->
            ttt = componentContextFactory.invoke(lifecycle, stateKeeper, instanceKeeper, backHandler)
        }
        newComponentContext.invoke(lifecycle.asEssentyLifecycle(), stateKeeper, instanceKeeper, backHandler)

        return DefaultCounterComponent(ttt!!, {})
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

interface TestComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        class ListChild(val component: ItemListComponent) : Child()
    }
}

class DefaultTestComponent(
    componentContext: ComponentContext
) : TestComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, TestComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(), // Or null to disable navigation state saving
            initialConfiguration = Config.List,
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild,
        )

    fun createTest() {
    }

    private fun createChild(config: Config, componentContext: ComponentContext): TestComponent.Child =
        when (config) {
            is Config.List -> TestComponent.Child.ListChild(itemList(componentContext))
        }

    private fun itemList(componentContext: ComponentContext): ItemListComponent =
        DefaultItemListComponent(
            componentContext = componentContext,
            onItemSelected = { navigation.push(Config.Details(itemId = it)) }
        )

    @Serializable // kotlinx-serialization plugin must be applied
    private sealed class Config {
        @Serializable
        data object List : Config()
    }
}
