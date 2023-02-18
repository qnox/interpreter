package me.qnox.interpreter.parser

import LangLexer
import LangParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream

/**
 * Antlr4 based grammar parser. Supports a grammar represented as pseudo BNF:
 * ```
 * expr ::= expr op expr | (expr) | identifier | { expr, expr } | number | map(expr, identifier -> expr) | reduce(expr, expr, identifier identifier -> expr)
 * op ::= + | - | * | / | ^
 * stmt ::= var identifier = expr | out expr | print "string"
 * program ::= stmt | program stmt
 * ```
 */
class Parser {

    /**
     * Parses program from input stream and returns Antlr4 generated Rule Context Tree
     *
     * @throws ParsingException if syntax error was occurred.
     */
    fun parse(inputStream: InputStream): LangParser.ProgramContext {
        val lexer = LangLexer(CharStreams.fromStream(inputStream))
        val parser = LangParser(CommonTokenStream(lexer))
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