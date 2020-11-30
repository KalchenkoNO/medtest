package com.ce2af4a3.tests.presentation.starttesting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.databinding.FragmentStartTestingBinding
import com.ce2af4a3.tests.databinding.GroupErrorBinding
import com.ce2af4a3.tests.presentation.testing.TestingViewModel
import com.ce2af4a3.tests.utils.isVisible
import com.ce2af4a3.tests.utils.lazyNavController
import com.ce2af4a3.tests.utils.supportActionBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartTestingFragment : Fragment() {
    private val sharedVm by navGraphViewModels<TestingViewModel>(R.id.nav_testing) {
        defaultViewModelProviderFactory
    }

    private val navController by lazyNavController()
    private lateinit var viewBinding: FragmentStartTestingBinding
    private lateinit var errorGroupBinding: GroupErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStartTestingBinding.inflate(
        inflater,
        container,
        false
    ).run {
        viewBinding = this
        errorGroupBinding = GroupErrorBinding.bind(root)
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() = with(viewBinding) {
        errorGroupBinding.errorRetry.setOnClickListener { sharedVm.onClick(it.id) }
        startTesting.setOnClickListener { view ->
            sharedVm.onClick(view.id)
        }
        testingDuration.doAfterTextChanged { text ->
            sharedVm.onTextChanged(R.id.testing_duration, text.toString())
        }
        repetitions.doAfterTextChanged { text ->
            sharedVm.onTextChanged(R.id.repetitions, text.toString())
        }
        trainingMode.setOnCheckedChangeListener { _, isChecked ->
            sharedVm.onCheck(R.id.training_mode, isChecked)
        }
    }

    private fun observeViewModel() = with(sharedVm) {
        getLiveLoadingIndicatorVisible().observe(viewLifecycleOwner) { isVisible ->
            viewBinding.loadingIndicator.isVisible(isVisible)
        }
        getLiveAppbarTitle().observe(viewLifecycleOwner) { supportActionBar()?.title = it }
        getLiveTestingDurationMin().observe(
            viewLifecycleOwner,
            viewBinding.testingDuration::setText
        )
        getLiveStartButtonEnabled().observe(
            viewLifecycleOwner,
            viewBinding.startTesting::setEnabled
        )
        //sharedVm.getLiveRepetitionsLayoutVisible().observe(viewLifecycleOwner, timer::setEnabled)
        getLiveCurrentQuestion().observe(viewLifecycleOwner) {
            navController.navigate(
                StartTestingFragmentDirections.actionStartTestingFragmentToQuestionFragment()
            )
        }
        getLiveRepetitionsLayoutVisible().observe(viewLifecycleOwner) { isVisible ->
            viewBinding.repetitionsLayout.isVisible = isVisible
        }
        getLiveRepetitionsInTrainingMode().observe(
            viewLifecycleOwner,
            viewBinding.repetitions::setText
        )
        getLiveTrainingMode().observe(viewLifecycleOwner, viewBinding.trainingMode::setChecked)
        getLiveErrorGroupVisible().observe(viewLifecycleOwner) { isVisible ->
            errorGroupBinding.errorGroup.isVisible = isVisible
        }
        getLiveContentGroupVisible().observe(viewLifecycleOwner) { isVisible ->
            viewBinding.contentGroup.isVisible = isVisible
        }
    }
}