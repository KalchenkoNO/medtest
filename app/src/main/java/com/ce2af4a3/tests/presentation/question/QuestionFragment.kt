package com.ce2af4a3.tests.presentation.question

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.databinding.FragmentQuestionBinding
import com.ce2af4a3.tests.presentation.OffsetItemDecoration
import com.ce2af4a3.tests.presentation.testing.TestingViewModel
import com.ce2af4a3.tests.utils.lazyNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuestionFragment : Fragment() {
    private val sharedViewModel by navGraphViewModels<TestingViewModel>(R.id.nav_testing) {
        defaultViewModelProviderFactory
    }
    private lateinit var viewBinding: FragmentQuestionBinding

    private val nav by lazyNavController()

    @Inject
    lateinit var answerAdapter: AnswerAdapter

    private var isFilled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentQuestionBinding.inflate(
        inflater,
        container,
        false
    ).run {
        viewBinding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeViewModel()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { goBack() }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().findViewById<Toolbar>(R.id.toolbar)
            .setNavigationOnClickListener { goBack() }
    }

    private fun goBack() {
        nav.popBackStack(R.id.nav_testing, true)
    }

    private fun initView() = with(viewBinding) {
        answers.apply {
            adapter = answerAdapter.apply {
                setSelectionFinishedListener { answers ->
                    sharedViewModel.onAnswersSubmitted(answers)
                }
            }
            addItemDecoration(OffsetItemDecoration(resources.getDimensionPixelOffset(R.dimen.margin_small)))
        }
        submit.setOnClickListener {
            sharedViewModel.onClick(it.id)
            answerAdapter.checkAnswers()
        }
        listOf(next, count, skip).forEach { button ->
            button.setOnClickListener { sharedViewModel.onClick(it.id) }
        }
    }

    private fun observeViewModel() = with(viewBinding) {
        sharedViewModel.getLiveCurrentQuestion().observe(viewLifecycleOwner) { questionData ->
            if (!isFilled) {
                question.text = questionData.text
                answerAdapter.submitList(questionData.answers)
                isFilled = true
            } else {
                nav.navigate(QuestionFragmentDirections.actionQuestionFragmentSelf())
            }
        }
        sharedViewModel.getLiveQuestionProgress().observe(viewLifecycleOwner, counter::setText)
        sharedViewModel.getLiveSubmitButtonVisibility().observe(viewLifecycleOwner) { isVisible ->
            submit.isVisible = isVisible
        }
        sharedViewModel.getLiveCountButtonVisibility().observe(viewLifecycleOwner) { isVisible ->
            count.isVisible = isVisible
        }
        sharedViewModel.getLiveNextButtonVisibility().observe(viewLifecycleOwner) { isVisible ->
            next.isVisible = isVisible
        }
        sharedViewModel.getLiveSkipButtonVisibility().observe(viewLifecycleOwner) { isVisible ->
            skip.isVisible = isVisible
        }
        sharedViewModel.getLiveTimeRemaining().observe(viewLifecycleOwner) { time ->
            timer.text = time
        }
        sharedViewModel.getLiveResult().observe(viewLifecycleOwner) {
            nav.navigate(
                QuestionFragmentDirections.actionQuestionFragmentToTestingResultFragment()
            )
        }
    }
}