package com.ce2af4a3.tests.utils

import com.ce2af4a3.tests.domain.Answer
import com.ce2af4a3.tests.domain.Question
import com.ce2af4a3.tests.domain.TestData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import javax.inject.Inject

class TestDataParser @Inject constructor() : Parser<String, TestData> {
    override fun parse(input: String): TestData = Jsoup.parse(input).let { document ->
        TestData(
            title = document.title(),
            questions = parseQuestions(document),
        )
    }

    private fun parseQuestions(document: Document): List<Question> {
        return document.body().children().asSequence().chunked { _, next ->
            next.tag().name == TAG_TABLE
        }.mapNotNull { chunk ->
            mapChunk(chunk)
        }.toList()
    }

    private fun mapChunk(chunk: List<Element>): Question? {
        var questionText: String? = null
        var answers: List<Answer>? = null
        chunk.forEach {
            when (it.tag().name) {
                TAG_PARAGRAPH -> {
                    if (!questionText.isNullOrBlank()) return@forEach
                    questionText = it.text()
                }
                TAG_LIST -> {
                    if (answers != null) return@forEach
                    answers = mapAnswers(it.getElementsByTag(TAG_LIST_ITEM))
                }
            }
        }

        return runCatching {
            Question(
                text = questionText!!,
                answers = answers?.takeIf { it.size >= 2 }!!
            )
        }.getOrNull()
    }

    private fun mapAnswers(elements: Elements): List<Answer>? {
        return elements.map { item ->
            Answer(
                text = item.text(),
                isCorrect = item.hasClass(CLASS_CORRECT)
            )
        }.takeIf { answers ->
            answers.find { it.isCorrect } != null
        }
    }

    companion object {
        private const val TAG_TABLE = "table"
        private const val TAG_PARAGRAPH = "p"
        private const val TAG_LIST = "ul"
        private const val TAG_LIST_ITEM = "li"
        private const val CLASS_CORRECT = "correct"
    }
}