package com.ce2af4a3.tests.presentation.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.databinding.FragmentTestingResultBinding
import com.ce2af4a3.tests.presentation.question.AnswerAdapter
import com.ce2af4a3.tests.presentation.testing.TestingViewModel
import com.ce2af4a3.tests.utils.lazyNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestingResultFragment : Fragment() {
    private val sharedViewModel by navGraphViewModels<TestingViewModel>(R.id.nav_testing) {
        defaultViewModelProviderFactory
    }
    private lateinit var viewBinding: FragmentTestingResultBinding

    private val nav by lazyNavController()

    @Inject
    lateinit var answerAdapter: AnswerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTestingResultBinding.inflate(
        inflater,
        container,
        false
    ).run {
        viewBinding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initView()
    }

    private fun initView() = with(viewBinding) {
        ok.setOnClickListener {
            nav.popBackStack(R.id.nav_testing, true)
        }
    }

    private fun observeViewModel() = with(viewBinding) {
        sharedViewModel.getLiveResult().observe(viewLifecycleOwner) { result ->
            questions.text = getString(R.string.number_of_questions, result.totalQuestions)
            correct.text = getString(R.string.number_of_correct, result.totalCorrect)
            mistakes.text = getString(R.string.number_of_mistakes, result.totalMistakes)
        }
    }
}