package com.ce2af4a3.tests.domain

class Question(
    val text: String,
    val answers: List<Answer>,
    val timesCorrectlyAnswered: Int = 0
)