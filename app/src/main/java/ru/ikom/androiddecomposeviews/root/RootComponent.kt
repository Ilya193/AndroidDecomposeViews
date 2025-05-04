package ru.ikom.androiddecomposeviews.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.ikom.androiddecomposeviews.messages.MessagesComponent
import ru.ikom.androiddecomposeviews.messages.DefaultMessagesComponent
import ru.ikom.androiddecomposeviews.data.DefaultMessagesRepository
import ru.ikom.androiddecomposeviews.details.DefaultDetailsComponent
import ru.ikom.androiddecomposeviews.details.DetailsComponent
import ru.ikom.androiddecomposeviews.domain.MessagesRepository

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Messages(val component: MessagesComponent) : Child()
        class Details(val component: DetailsComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val repository: MessagesRepository = DefaultMessagesRepository()

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
            Config.Counter -> RootComponent.Child.Messages(counterComponent(componentContext))
            is Config.Details -> RootComponent.Child.Details(detailsComponent(componentContext, config.id))
        }

    private fun counterComponent(componentContext: ComponentContext): MessagesComponent =
        DefaultMessagesComponent(
            componentContext = componentContext,
            repository = repository,
            onOpenDetails = { navigation.pushNew(Config.Details(it)) }
        )

    private fun detailsComponent(componentContext: ComponentContext, id: Int): DetailsComponent =
        DefaultDetailsComponent(
            componentContext = componentContext,
            repository = repository,
            id = id,
            onNavigateBack = { navigation.pop() }
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Counter : Config

        @Serializable
        data class Details(val id: Int) : Config
    }
}