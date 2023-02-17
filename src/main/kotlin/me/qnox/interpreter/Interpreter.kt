package me.qnox.interpreter

import java.io.InputStream

class Interpreter(
    private val io: IO = IO(),
    private val parser: Parser = Parser(),
    private val expressionEvaluator: ExpressionEvaluator = ExpressionEvaluator(),
) {

    fun eval(inputStream: InputStream) {
        try {
            val program = parser.parse(inputStream)
            InterpreterVisitor(expressionEvaluator = expressionEvaluator, io = io).visit(program)
        } catch (e: EvaluationException) {
            io.err.println(e.message)
        } catch (e: ParsingException) {
            io.err.println(e.message)
        }
    }

}

fun main() {
    Interpreter(io = IO(System.out, System.err)).eval(System.`in`)
}