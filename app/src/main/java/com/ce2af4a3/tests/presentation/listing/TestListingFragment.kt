package com.ce2af4a3.tests.presentation.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.databinding.FragmentTestListingBinding
import com.ce2af4a3.tests.databinding.GroupErrorBinding
import com.ce2af4a3.tests.presentation.OffsetItemDecoration
import com.ce2af4a3.tests.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestListingFragment @Inject constructor() : Fragment() {
    private lateinit var viewBinding: FragmentTestListingBinding
    private lateinit var errorGroupBinding: GroupErrorBinding
    private val viewModel by viewModels<TestListingViewModel>()

    @Inject
    lateinit var testsAdapter: TestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTestListingBinding.inflate(inflater, container, false).run {
            viewBinding = this
            errorGroupBinding = GroupErrorBinding.bind(root)
            root
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testsAdapter.setOnClickListener { test ->
            findNavController().navigate(
                TestListingFragmentDirections.actionStartTesting(test.urn)
            )
        }
        errorGroupBinding.errorRetry.setOnClickListener { viewModel.onClick(it.id) }
        viewBinding.testsList.apply {
            adapter = testsAdapter
            addItemDecoration(OffsetItemDecoration(resources.getDimensionPixelOffset(R.dimen.margin_default)))
            layoutManager = LinearLayoutManager(requireContext())
        }
        observeViewModel()
    }

    private fun observeViewModel() = with(viewModel) {
        liveTests.observe(viewLifecycleOwner) { availableTests ->
            testsAdapter.submitList(availableTests)
        }
        liveProgressIndicatorVisible.observe(viewLifecycleOwner) { isVisible ->
            viewBinding.loadingIndicator.isVisible(isVisible)
        }
        liveErrorGroupVisible.observe(viewLifecycleOwner) { isVisible ->
            errorGroupBinding.errorGroup.isVisible = isVisible
        }
        liveListingVisible.observe(viewLifecycleOwner) { isVisible ->
            viewBinding.testsList.isVisible = isVisible
        }
    }
}