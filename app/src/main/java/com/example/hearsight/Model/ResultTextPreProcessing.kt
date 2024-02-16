package com.example.hearsight.Model

import android.content.Context

class ResultTextPreProcessing(private val context:Context) {
    fun removeUnwantedSpaces(input: String): String {
        var result = input.trim()
        result = result.replace(Regex("\\s+"), " ")
        result = result.replace(Regex("(\\p{Punct})"), "$1 ")
        return result
    }
}