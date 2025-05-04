@file:OptIn(ExperimentalDecomposeApi::class)

package ru.ikom.androiddecomposeviews.details

import android.view.View
import android.widget.TextView
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.android.ViewContext
import com.arkivanov.decompose.extensions.android.layoutInflater
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.R
import ru.ikom.androiddecomposeviews.childCoroutineScope

fun ViewContext.detailsView(
    component: DetailsComponent
): View {
    val layout = layoutInflater.inflate(R.layout.details_screen, parent, false)
    val details = layout.findViewById<TextView>(R.id.details)

    val scope = childCoroutineScope(lifecycle)

    layout.setOnClickListener { component.onBack() }

    scope.launch {
        component.states.collect {
            details.text = it.text
        }
    }

    component.onViewCreated()

    return layout
}