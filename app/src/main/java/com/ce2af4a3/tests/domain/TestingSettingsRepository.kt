package com.ce2af4a3.tests.domain

interface TestingSettingsRepository {
    suspend fun getTestingSettings(): TestingSettings
    suspend fun setTestingSettings(
        testingDurationMin: Int?,
        trainingMode: Boolean?,
        repetitionsInTrainingMode: Int?
    )
}