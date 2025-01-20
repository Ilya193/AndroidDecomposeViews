package ru.ikom.androiddecomposeviews.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.ikom.androiddecomposeviews.R
import ru.ikom.androiddecomposeviews.counter.CounterFragment
import ru.ikom.androiddecomposeviews.databinding.RootFragmentBinding
import ru.ikom.androiddecomposeviews.details.DetailsFragment

class RootFragment : Fragment() {

    private var _binding: RootFragmentBinding? = null
    private val binding: RootFragmentBinding get() = _binding!!

    private val fragmentFactoryImpl = FragmentFactoryImpl()

    private val component: RootComponent by lazy(LazyThreadSafetyMode.NONE) {
        DefaultRootComponent(
            defaultComponentContext(onBackPressedDispatcher = requireActivity().onBackPressedDispatcher)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component
        childFragmentManager.fragmentFactory = fragmentFactoryImpl
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RootFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupComponent()

        if (savedInstanceState == null) {
            openCounterFragment()
        }
    }

    private fun setupComponent() {
        viewLifecycleOwner.lifecycleScope.launch {
            component.labels.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .filterNotNull()
                .collect {
                    when (it) {
                        is RootComponent.Label.OnOpenDetails -> onOpenDetails()
                        is RootComponent.Label.Back -> back()
                    }
                }
        }
    }

    private fun openCounterFragment() {
        childFragmentManager.commit {
            replace(R.id.content, fragmentFactoryImpl.counterFragment())
        }
    }

    private fun onOpenDetails() {
        childFragmentManager.commit {
            replace(R.id.content, fragmentFactoryImpl.detailsFragment())
            addToBackStack(null)
        }
    }

    private fun back() {
        childFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class FragmentFactoryImpl : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (loadFragmentClass(classLoader, className)) {
                CounterFragment::class.java -> counterFragment()
                DetailsFragment::class.java -> detailsFragment()
                else -> super.instantiate(classLoader, className)
            }

        fun counterFragment(): CounterFragment =
            CounterFragment(
                onOpenDetails = ::onOpenDetails
            )

        fun detailsFragment(): DetailsFragment =
            DetailsFragment()
    }
}