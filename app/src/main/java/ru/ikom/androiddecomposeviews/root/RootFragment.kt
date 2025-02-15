package ru.ikom.androiddecomposeviews.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.retainedComponent
import ru.ikom.androiddecomposeviews.counter.CounterScreen
import ru.ikom.androiddecomposeviews.details.DetailsScreen

class RootFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val component = retainedComponent {
            DefaultRootComponent(it)
        }

        return content {
            RootContent(component)
        }
    }
}

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.stack,
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.Counter -> CounterScreen(child.component)
            is RootComponent.Child.Details -> DetailsScreen(child.component)
        }
    }
}