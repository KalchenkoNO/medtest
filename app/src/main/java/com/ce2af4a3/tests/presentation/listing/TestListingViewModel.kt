package com.ce2af4a3.tests.presentation.listing

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ce2af4a3.tests.R
import com.ce2af4a3.tests.domain.Test
import com.ce2af4a3.tests.domain.TestListingRepository
import kotlinx.coroutines.launch

class TestListingViewModel @ViewModelInject constructor(
    private val repo: TestListingRepository
) : ViewModel() {
    val liveTests = MutableLiveData<List<Test>>()
    val liveProgressIndicatorVisible = MutableLiveData<Boolean>()
    val liveErrorGroupVisible = MutableLiveData<Boolean>()
    val liveListingVisible = MutableLiveData<Boolean>()

    init {
        loadTests()
    }

    private fun loadTests() = viewModelScope.launch {
        reset()
        runCatching {
            liveTests.value = repo.getTests()
            liveListingVisible.value = true
        }.onFailure(::onError)
        liveProgressIndicatorVisible.value = false
    }

    private fun reset() {
        liveProgressIndicatorVisible.value = true
        liveErrorGroupVisible.value = false
        liveTests.value = emptyList()
        liveListingVisible.value = false
    }

    private fun onError(throwable: Throwable) {
        liveErrorGroupVisible.value = true
        liveListingVisible.value = false
    }

    fun onClick(viewId: Int) {
        when (viewId) {
            R.id.error_retry -> loadTests()
        }
    }
}