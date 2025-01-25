package ru.ikom.androiddecomposeviews.counter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class CounterViewModel(
    private val counterComponent: CounterComponent
) : ViewModel(), CounterComponent by counterComponent {

}