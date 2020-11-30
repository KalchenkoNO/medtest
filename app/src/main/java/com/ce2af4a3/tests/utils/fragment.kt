package com.ce2af4a3.tests.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.lazyNavController() = lazy { findNavController() }

fun Fragment.supportActionBar() = (requireActivity() as AppCompatActivity).supportActionBar