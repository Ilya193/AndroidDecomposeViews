package ru.ikom.androiddecomposeviews.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.retainedComponent
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.R
import ru.ikom.androiddecomposeviews.counter.CounterFragment
import ru.ikom.androiddecomposeviews.databinding.DetailsFragmentBinding

class DetailsFragment: Fragment() {

    private var _binding: DetailsFragmentBinding? = null
    private val binding: DetailsFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun observeComponent(component: DetailsComponent) {
        viewLifecycleOwner.lifecycleScope.launch {
            component.state.collect {
                binding.details.text = it.text
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun DetailsScreen(component: DetailsComponent) {
    AndroidFragment<DetailsFragment>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.observeComponent(component)
    }
}