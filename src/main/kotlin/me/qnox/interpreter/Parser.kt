package me.qnox.interpreter

import langLexer
import langParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream

class Parser {

    fun parse(inputStream: InputStream): langParser.ProgramContext {
        val lexer = langLexer(CharStreams.fromStream(inputStream))
        val parser = langParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        val syntaxErrorListener = SyntaxErrorListener()
        parser.addErrorListener(syntaxErrorListener)
        val program = parser.program()
        if (syntaxErrorListener.syntaxErrors.isNotEmpty()) {
            throw ParsingException(
                syntaxErrorListener.syntaxErrors.joinToString(
                    separator = "\n",
                    prefix = "Syntax errors found:\n"
                ) { it.toString() })
        }
        return program
    }

}