package ru.ikom.androiddecomposeviews

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start
import com.arkivanov.essenty.lifecycle.stop

interface Observer<T> {
    fun onNext(value: T)
}

inline fun <T> observer(crossinline onNext: (T) -> Unit): Observer<T> = object : Observer<T> {
    override fun onNext(value: T) {
        onNext(value)
    }

}

infix fun Lifecycle.to(lifecycleRegistry: LifecycleRegistry) {
    addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) =
            when (event) {
                Lifecycle.Event.ON_CREATE -> lifecycleRegistry.create()
                Lifecycle.Event.ON_START -> lifecycleRegistry.start()
                Lifecycle.Event.ON_RESUME -> lifecycleRegistry.resume()
                Lifecycle.Event.ON_PAUSE -> lifecycleRegistry.pause()
                Lifecycle.Event.ON_STOP -> lifecycleRegistry.stop()
                else -> {}
            }
    })

}