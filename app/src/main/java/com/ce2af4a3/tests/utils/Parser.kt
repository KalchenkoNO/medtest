package com.ce2af4a3.tests.utils

interface Parser<I, O>: (I)-> O{
    fun parse(input: I): O

    override fun invoke(input: I): O = parse(input)
}