package ru.ikom.androiddecomposeviews.counter

import ru.ikom.androiddecomposeviews.BaseView

interface CounterView : BaseView<CounterView.Model> {

    data class Model(
        val text: String,
    )
}

val stateToModel: CounterComponent.State.() -> CounterView.Model =
    {
        CounterView.Model(
            text = "$counter",
        )
    }