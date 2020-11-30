package com.ce2af4a3.tests.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ce2af4a3.tests.domain.TestingSettings
import com.ce2af4a3.tests.domain.TestingSettingsRepository
import javax.inject.Inject

class TestingSettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : TestingSettingsRepository {
    override suspend fun getTestingSettings(): TestingSettings {
        return TestingSettings(
            testingDurationMin = sharedPreferences.getInt(
                KEY_TESTING_DURATION,
                DEFAULT_TESTING_DURATION
            ),
            trainingMode = sharedPreferences.getBoolean(KEY_TRAINING_MODE, DEFAULT_TRAINING_MODE),
            repetitionsInTrainingMode = sharedPreferences.getInt(
                KEY_TRAINING_MODE_REPETITIONS,
                DEFAULT_TRAINING_REPETITIONS
            )
        )
    }

    override suspend fun setTestingSettings(
        testingDurationMin: Int?,
        trainingMode: Boolean?,
        repetitionsInTrainingMode: Int?
    ) {
        sharedPreferences.edit {
            putInt(KEY_TESTING_DURATION, testingDurationMin ?: DEFAULT_TESTING_DURATION)
            putBoolean(KEY_TRAINING_MODE, trainingMode ?: DEFAULT_TRAINING_MODE)
            putInt(
                KEY_TRAINING_MODE_REPETITIONS,
                repetitionsInTrainingMode ?: DEFAULT_TRAINING_REPETITIONS
            )
        }
    }

    companion object {
        const val KEY_TESTING_DURATION = "testingDuration"
        const val KEY_TRAINING_MODE = "trainingMode"
        const val KEY_TRAINING_MODE_REPETITIONS = "trainingModeTries"
        const val DEFAULT_TESTING_DURATION = 60
        const val DEFAULT_TRAINING_MODE = false
        const val DEFAULT_TRAINING_REPETITIONS = 3
    }
}