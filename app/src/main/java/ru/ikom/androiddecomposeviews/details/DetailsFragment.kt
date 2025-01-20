package ru.ikom.androiddecomposeviews.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.retainedComponent
import ru.ikom.androiddecomposeviews.R

class DetailsFragment(
) : Fragment(R.layout.details_fragment) {

    private val component: DetailsComponent by lazy(LazyThreadSafetyMode.NONE) {
        retainedComponent { componentContext ->
            DefaultDetailsComponent(
                componentContext = componentContext,
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component
    }
}