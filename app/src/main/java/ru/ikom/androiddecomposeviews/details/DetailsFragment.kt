package ru.ikom.androiddecomposeviews.details

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import ru.ikom.androiddecomposeviews.R

class DetailsFragment(
    getComponent: (Lifecycle) -> DetailsComponent,
) : Fragment(R.layout.details_fragment) {

    private val component: DetailsComponent = getComponent(lifecycle)
}