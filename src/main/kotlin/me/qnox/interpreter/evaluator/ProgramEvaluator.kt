package me.qnox.interpreter.evaluator

import me.qnox.interpreter.IO
import me.qnox.interpreter.parser.antlr4.LangParser

/**
 * Rule Context Tree based program evaluator for grammar parsed by [me.qnox.interpreter.parser.Parser].
 * Uses provided [IO] to produce output. Expression evaluation was moved to a separate class [ExpressionEvaluator]
 * for simplicity.
 */
class ProgramEvaluator(
    private val io: IO = IO()
) {

    fun evaluate(program: LangParser.ProgramContext) {
        StatementEvaluatorVisitor(io = io).visit(program)
    }
}