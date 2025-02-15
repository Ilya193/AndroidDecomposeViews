package ru.ikom.androiddecomposeviews.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.ikom.androiddecomposeviews.counter.CounterComponent
import ru.ikom.androiddecomposeviews.counter.DefaultCounterComponent
import ru.ikom.androiddecomposeviews.details.DefaultDetailsComponent
import ru.ikom.androiddecomposeviews.details.DetailsComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Counter(val component: CounterComponent) : Child()
        class Details(val component: DetailsComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Counter,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Counter -> RootComponent.Child.Counter(counterComponent(componentContext))
            Config.Details -> RootComponent.Child.Details(detailsComponent(componentContext))
        }

    private fun counterComponent(componentContext: ComponentContext): CounterComponent =
        DefaultCounterComponent(
            componentContext = componentContext,
            onOpenDetails = { navigation.pushNew(Config.Details) }
        )

    private fun detailsComponent(componentContext: ComponentContext): DetailsComponent =
        DefaultDetailsComponent(
            componentContext = componentContext,
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Counter : Config

        @Serializable
        data object Details : Config
    }
}