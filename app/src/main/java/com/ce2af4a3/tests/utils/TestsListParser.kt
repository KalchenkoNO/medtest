package com.ce2af4a3.tests.utils

import com.ce2af4a3.tests.domain.Test
import org.jsoup.Jsoup
import javax.inject.Inject

class TestsListParser @Inject constructor() : Parser<String, List<Test>> {
    override fun parse(input: String): List<Test> = Jsoup.parse(input).body()
        .getElementsByTag(TAG_LIST_ITEM)
        .flatMap {element ->
            element.getElementsByTag(TAG_LINK)
        }.map { element ->
            Test(
                name = element.text(),
                urn = element.attr(ATTR_LINK)
            )
        }

    companion object {
        private const val TAG_LIST_ITEM = "ul"
        private const val TAG_LINK = "a"
        private const val ATTR_LINK = "href"
    }
}