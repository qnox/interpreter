package me.qnox.interpreter.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token


class SyntaxErrorListener : BaseErrorListener() {
    private val _syntaxErrors: MutableList<SyntaxError> = mutableListOf()
    val syntaxErrors: List<SyntaxError>
        get() = _syntaxErrors

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) {
        _syntaxErrors.add(SyntaxError(recognizer, offendingSymbol as Token, line, charPositionInLine, msg, e))
    }
}

