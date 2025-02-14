package ru.ikom.androiddecomposeviews.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.retainedComponent
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.ViewRenderer
import ru.ikom.androiddecomposeviews.databinding.CounterFragmentBinding
import ru.ikom.androiddecomposeviews.diff

class CounterFragment(
    private val getComponent: (Lifecycle) -> CounterComponent,
    private val onOpenDetails: () -> Unit,
) : Fragment(), CounterView {

    private var _binding: CounterFragmentBinding? = null
    private val binding: CounterFragmentBinding get() = _binding!!

    private val component: CounterComponent by lazy(LazyThreadSafetyMode.NONE) {
        retainedComponent { componentContext ->
            DefaultCounterComponent(
                componentContext = componentContext,
                onOpenDetails = onOpenDetails,
            )
        }
    }

    private val test: CounterComponent by lazy {
        getComponent(lifecycle)
    }

    override var viewRenderer: ViewRenderer<CounterView.Model>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CounterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCounterView(binding, test)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        release()
        _binding = null
    }
}

fun CounterFragment.initCounterView(
    binding: CounterFragmentBinding,
    component: CounterComponent,
) {
    viewRenderer = diff {
        diff(
            get = CounterView.Model::text,
            set = binding::updateCounter
        )
    }

    binding.counter.setOnClickListener { component.openDetails() }

    viewLifecycleOwner.lifecycleScope.launch {
        component.state.collect {
            render(stateToModel(it))
        }
    }
}

fun CounterFragmentBinding.updateCounter(text: String) {
    counter.text = text
}