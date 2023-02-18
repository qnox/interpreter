package me.qnox.interpreter

import me.qnox.interpreter.evaluator.EvaluationException
import me.qnox.interpreter.evaluator.ProgramEvaluator
import me.qnox.interpreter.parser.Parser
import me.qnox.interpreter.parser.ParsingException
import java.io.InputStream
import kotlin.system.exitProcess

/**
 * Interpreter entry point delegates parsing and evaluation to corresponding classes.
 */
class Interpreter(
    private val io: IO = IO()
) {

    private val parser: Parser = Parser()

    private val programEvaluator: ProgramEvaluator = ProgramEvaluator(io = io)

    /**
     * Interprets program from input stream.
     *
     * @return 0 if interpretation was successful or 1 if error happened
     */
    fun eval(inputStream: InputStream): Int {
        return try {
            val program = parser.parse(inputStream)
            programEvaluator.evaluate(program)
            0
        } catch (e: EvaluationException) {
            handleException(e)
        } catch (e: ParsingException) {
            handleException(e)
        }
    }

    private fun handleException(e: Exception): Int {
        io.err.println(e.message)
        return 1
    }

}

fun main() {
    val result = Interpreter(io = IO(System.out, System.err)).eval(System.`in`)
    exitProcess(result)
}