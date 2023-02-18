package me.qnox.interpreter.parser

import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token

data class SyntaxError(
    val recognizer: Recognizer<*, *>?,
    val token: Token?,
    val line: Int,
    val charPositionInLine: Int,
    val msg: String?,
    val e: RecognitionException?
) {

    override fun toString(): String {
        return "line $line:$charPositionInLine $msg"
    }
}