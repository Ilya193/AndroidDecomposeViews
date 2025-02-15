package ru.ikom.androiddecomposeviews.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.databinding.CounterFragmentBinding
import ru.ikom.androiddecomposeviews.diff

class CounterFragment : Fragment() {

    private var _binding: CounterFragmentBinding? = null
    private val binding: CounterFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CounterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun observeComponent(component: CounterComponent) {
        val viewRenderer = diff {
            diff(
                get = CounterView.Model::text,
                set = binding.counter::setText
            )
        }

        binding.counter.setOnClickListener { component.openDetails() }

        viewLifecycleOwner.lifecycleScope.launch {
            component.state.collect {
                viewRenderer.render(stateToModel(it))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun CounterScreen(component: CounterComponent) {
    AndroidFragment<CounterFragment>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.observeComponent(component)
    }
}