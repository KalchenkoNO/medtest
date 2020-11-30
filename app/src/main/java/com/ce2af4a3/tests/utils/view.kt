package com.ce2af4a3.tests.utils

import androidx.core.widget.ContentLoadingProgressBar

fun ContentLoadingProgressBar.isVisible(isVisible: Boolean) {
    if (isVisible) show() else hide()
}