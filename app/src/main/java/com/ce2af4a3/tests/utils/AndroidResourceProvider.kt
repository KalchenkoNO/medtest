package com.ce2af4a3.tests.utils

import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import javax.inject.Inject

class AndroidResourceProvider @Inject constructor(
    private val resources: Resources
) : ResourceProvider {
    @Throws(NotFoundException::class)
    override fun getString(id: Int): String = resources.getString(id)
}