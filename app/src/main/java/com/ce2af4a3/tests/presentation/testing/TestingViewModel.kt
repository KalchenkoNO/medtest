package com.ce2af4a3.tests.presentation.testing

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.domain.*
import com.ce2af4a3.tests.utils.ResourceProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class TestingViewModel @ViewModelInject constructor(
    @Assisted private val stateHandle: SavedStateHandle,
    private val testRepository: TestDataRepository,
    private val settingRepository: TestingSettingsRepository,
    resourceProvider: ResourceProvider
) : ViewModel() {
    private val questionProgressFormat = resourceProvider.getString(R.string.n_of_n)

    private val liveLoadingIndicatorVisible = MutableLiveData<Boolean>()
    private val liveErrorGroupVisible = MutableLiveData<Boolean>()
    private val liveContentGroupVisible = MutableLiveData<Boolean>()
    private val liveAppbarTitle = MutableLiveData<String>()
    private val liveTestingDurationMin = MutableLiveData<String>()
    private val liveTrainingMode = MutableLiveData<Boolean>()
    private val liveRepetitionsInTrainingMode = MutableLiveData<String>()
    private val liveStartButtonEnabled = MutableLiveData<Boolean>()
    private val liveRepetitionsLayoutVisible = MutableLiveData<Boolean>()
    private val liveCurrentQuestion = MutableLiveData<Question>()

    private val liveTimeRemaining = MutableLiveData<String>()
    private val liveQuestionProgress = MutableLiveData<String>()
    private val liveSubmitButtonVisibility = MutableLiveData<Boolean>()
    private val liveSkipButtonVisibility = MutableLiveData<Boolean>()
    private val liveNextButtonVisibility = MutableLiveData<Boolean>()
    private val liveCountButtonVisibility = MutableLiveData<Boolean>()
    private val liveResult = MutableLiveData<TestingResult>()

    private var trainingMode = false
    private var testingDuration: Int? = null
    private var repetitionsInTrainingMode: Int? = null
    private var questionQueue: Queue<Question>? = null
    private var timerJob: Job? = null

    private var numberOfCorrectAnswersInCurrentQuestion = 0
    private var totalQuestionQuantity = 0
    private var totalCorrectAnswers = 0
    private var currentQuestionNumber = 0
    private var totalMistakesMade = 0
    private var answerMadeIsCorrect = false

    init {
        loadData()
    }

    fun getLiveLoadingIndicatorVisible() = liveLoadingIndicatorVisible.distinctUntilChanged()
    fun getLiveErrorGroupVisible() = liveErrorGroupVisible.distinctUntilChanged()
    fun getLiveContentGroupVisible() = liveContentGroupVisible.distinctUntilChanged()
    fun getLiveAppbarTitle() = liveAppbarTitle.distinctUntilChanged()
    fun getLiveTestingDurationMin() = liveTestingDurationMin.distinctUntilChanged()
    fun getLiveTrainingMode() = liveTrainingMode.distinctUntilChanged()
    fun getLiveRepetitionsInTrainingMode() = liveRepetitionsInTrainingMode.distinctUntilChanged()
    fun getLiveStartButtonEnabled() = liveStartButtonEnabled.distinctUntilChanged()
    fun getLiveRepetitionsLayoutVisible() = liveRepetitionsLayoutVisible.distinctUntilChanged()

    fun getLiveTimeRemaining() = liveTimeRemaining.distinctUntilChanged()
    fun getLiveQuestionProgress() = liveQuestionProgress.distinctUntilChanged()
    fun getLiveCurrentQuestion() = liveCurrentQuestion.distinctUntilChanged()

    fun getLiveSkipButtonVisibility() = liveSkipButtonVisibility.distinctUntilChanged()
    fun getLiveNextButtonVisibility() = liveNextButtonVisibility.distinctUntilChanged()
    fun getLiveCountButtonVisibility() = liveCountButtonVisibility.distinctUntilChanged()
    fun getLiveSubmitButtonVisibility() = liveSubmitButtonVisibility.distinctUntilChanged()

    fun getLiveResult() = liveResult.distinctUntilChanged()

    fun onClick(viewId: Int) {
        when (viewId) {
            R.id.error_retry -> loadData()
            R.id.start_testing -> startTesting()
            R.id.next -> onNextAction()
            R.id.count -> onCountAction()
            R.id.skip -> onSkipAction()
        }
    }

    private fun loadData() = viewModelScope.launch {
        liveLoadingIndicatorVisible.value = true
        liveErrorGroupVisible.value = false
        liveContentGroupVisible.value = false
        resetTimer()
        runCatching {
            val data = testRepository.getTestData(stateHandle[KEY_TEST_URN]!!)
            val settings = settingRepository.getTestingSettings()
            onDataLoaded(data, settings)
        }.onFailure(::onError)
        liveLoadingIndicatorVisible.value = false
    }

    private fun onDataLoaded(data: TestData, settings: TestingSettings) {
        liveAppbarTitle.value = data.title
        liveContentGroupVisible.value = true
        liveTestingDurationMin.value = settings.testingDurationMin.toString()
        liveTrainingMode.value = settings.trainingMode
        questionQueue = LinkedList(data.questions.shuffled())
        totalQuestionQuantity = data.questions.size
        toggleRepetitionsLayoutVisibility()
        settings.repetitionsInTrainingMode.let {
            repetitionsInTrainingMode = it
            liveRepetitionsInTrainingMode.value = it.toString()
        }
    }

    fun onCheck(viewId: Int, isChecked: Boolean) {
        when (viewId) {
            R.id.training_mode -> {
                trainingMode = isChecked
                toggleRepetitionsLayoutVisibility()
            }
        }
    }

    fun onTextChanged(viewId: Int, text: String) {
        when (viewId) {
            R.id.testing_duration -> {
                testingDuration = runCatching { text.toInt() }.getOrNull()
                toggleStartButtonVisibility()
            }
            R.id.repetitions -> {
                repetitionsInTrainingMode = runCatching { text.toInt() }.getOrNull()
                toggleStartButtonVisibility()
            }
        }
    }

    private fun toggleRepetitionsLayoutVisibility() {
        liveRepetitionsLayoutVisible.value = liveContentGroupVisible.value == true && trainingMode
    }

    private fun toggleStartButtonVisibility() {
        liveStartButtonEnabled.value = repetitionsInTrainingMode != null && testingDuration != null
    }

    private fun onError(throwable: Throwable) {
        liveErrorGroupVisible.value = true
    }

    private fun startTesting() {
        currentQuestionNumber = 1
        totalMistakesMade = 0
        resetTimer()
        saveSettings()
        if ((testingDuration ?: 0) > 0) startTimer()
        setNextQuestion()
    }

    private fun saveSettings() = viewModelScope.launch {
        settingRepository.setTestingSettings(
            testingDuration,
            trainingMode,
            repetitionsInTrainingMode
        )
    }

    private fun startTimer() = viewModelScope.launch {
        var currentTime = System.currentTimeMillis()
        val endTime = currentTime + ((testingDuration?.toLong() ?: 0L) * MINUTE)
        while (currentTime < endTime) {
            delay(SECOND)
            displayTimeRemaining(endTime - currentTime)
            currentTime = System.currentTimeMillis()
        }
        showResult()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun displayTimeRemaining(timeRemaining: Long) {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % SECONDS_IN_MINUTE
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % MINUTES_IN_HOUR
        val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining)
        liveTimeRemaining.value = timerFormat.format(hours, minutes, seconds)
    }

    private fun onSkipAction() {
        liveCurrentQuestion.value?.let { retainQuestionInQueue(it) }
        setNextQuestion()
    }

    private fun onNextAction() {
        if (trainingMode) {
            handleTrainingMode()
        } else {
            handleCommonMode()
        }
        setNextQuestion()
    }

    private fun onCountAction() {
        if (trainingMode) {
            handleTrainingMode(true)
        } else {
            handleCommonMode(true)
        }
        setNextQuestion()
    }

    private fun handleCommonMode(forceCorrect: Boolean = false) {
        if (forceCorrect || answerMadeIsCorrect) {
            totalCorrectAnswers++
        } else {
            totalMistakesMade++
        }
        currentQuestionNumber++
    }

    private fun handleTrainingMode(forceCorrect: Boolean = false) = liveCurrentQuestion.value?.let {
        val maxRepetitions = repetitionsInTrainingMode ?: 1
        val repetitions = if (forceCorrect || answerMadeIsCorrect) {
            it.timesCorrectlyAnswered + 1
        } else {
            it.timesCorrectlyAnswered
        }
        if (repetitions >= maxRepetitions) {
            currentQuestionNumber++
            totalCorrectAnswers++
        } else {
            retainQuestionInQueue(it.changeTimesCorrectlyAnswered(repetitions))
        }
    }

    private fun setNextQuestion() {
        questionQueue?.poll()?.run {
            numberOfCorrectAnswersInCurrentQuestion = answers.count { it.isCorrect }
            liveCurrentQuestion.value = prepareQuestion()
            setBeforeSubmitState()
        } ?: showResult()
    }

    private fun Question.prepareQuestion(): Question = Question(
        text = text,
        answers = answers.shuffled(),
        timesCorrectlyAnswered = timesCorrectlyAnswered
    )

    private fun Question.changeTimesCorrectlyAnswered(times: Int): Question = Question(
        text = text,
        answers = answers,
        timesCorrectlyAnswered = times
    )

    private fun retainQuestionInQueue(question: Question) {
        questionQueue?.add(question)
    }

    fun onAnswersSubmitted(answers: List<Answer>) {
        val correctAnswersMade = answers.count { it.isCorrect }
        answerMadeIsCorrect = answers.size == correctAnswersMade &&
                correctAnswersMade == numberOfCorrectAnswersInCurrentQuestion
        setAfterSubmitState(answerMadeIsCorrect)
    }

    private fun setBeforeSubmitState() {
        liveQuestionProgress.value = getCurrentProgressString()
        liveSkipButtonVisibility.value = true
        liveSubmitButtonVisibility.value = numberOfCorrectAnswersInCurrentQuestion > SINGLE_ANSWER
        liveCountButtonVisibility.value = false
        liveNextButtonVisibility.value = false
    }

    private fun getCurrentProgressString(): String = questionProgressFormat.format(
        currentQuestionNumber,
        totalQuestionQuantity
    )


    private fun setAfterSubmitState(isCorrect: Boolean) {
        liveSkipButtonVisibility.value = false
        liveSubmitButtonVisibility.value = false
        liveCountButtonVisibility.value = !isCorrect
        liveNextButtonVisibility.value = true
    }

    private fun showResult() {
        liveResult.value = TestingResult(
            totalQuestions = totalQuestionQuantity,
            totalMistakes = totalMistakesMade,
            totalCorrect = totalCorrectAnswers
        )
    }

    companion object {
        const val timerFormat = "%d:%02d:%02d"
        const val SECOND = 1000L
        const val SECONDS_IN_MINUTE = 60L
        const val MINUTE = SECONDS_IN_MINUTE * SECOND
        const val MINUTES_IN_HOUR = 60L
        private const val KEY_TEST_URN = "testUrn"
        const val SINGLE_ANSWER = 1
    }
}