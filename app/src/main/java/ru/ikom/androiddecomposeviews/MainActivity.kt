@file:OptIn(ExperimentalDecomposeApi::class)

package ru.ikom.androiddecomposeviews

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.size
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.Explode
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.android.DefaultViewContext
import com.arkivanov.decompose.extensions.android.ViewContext
import com.arkivanov.decompose.extensions.android.layoutInflater
import com.arkivanov.decompose.extensions.android.stack.StackRouterView
import com.arkivanov.decompose.retainedComponent
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import ru.ikom.androiddecomposeviews.messages.messagesView
import ru.ikom.androiddecomposeviews.details.detailsView
import ru.ikom.androiddecomposeviews.root.DefaultRootComponent
import ru.ikom.androiddecomposeviews.root.RootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val view = findViewById<ViewGroup>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val root = retainedComponent {
            DefaultRootComponent(it)
        }

        val viewContext =
            DefaultViewContext(
                parent = view,
                lifecycle = essentyLifecycle()
            )

        viewContext.apply {
            parent.addView(root(root))
        }
    }
}

fun ViewContext.root(
    component: RootComponent,
) : View {
    val layout = layoutInflater.inflate(R.layout.root_fragment, parent, false)
    val stackRouterView = layout as StackRouterView

    stackRouterView.children(component.stack, lifecycle) { parent, newStack, oldStack ->
        val oldView = parent.getChildAt(0)
        val newView: View

        when (val instance = newStack.active.instance) {
            is RootComponent.Child.Messages -> {
                newView = messagesView(instance.component)

                if (oldStack != null && oldView != null) {
                    TransitionManager.beginDelayedTransition(
                        parent,
                        TransitionSet()
                            .addTransition(Slide(Gravity.START).addTarget(newView))
                            .addTransition(Slide(Gravity.END).addTarget(oldView))
                            .setInterpolator(LinearInterpolator()),
                    )
                }
            }
            is RootComponent.Child.Details -> {
                newView = detailsView(instance.component)

                if (oldView != null && oldStack != null) {
                    TransitionManager.beginDelayedTransition(
                        parent,
                        TransitionSet()
                            .addTransition(Slide(Gravity.END).addTarget(newView))
                            .addTransition(Slide(Gravity.START).addTarget(oldView))
                            .setInterpolator(LinearInterpolator()),
                    )
                }
            }
        }

        parent.removeAllViews()
        parent.addView(newView)
    }

    return layout
}