package me.qnox.interpreter

import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals

class InterpreterTest {

    @Test
    fun `should calculate pi`() {
        ByteArrayOutputStream().use { out ->
            val interpreter = Interpreter(
                io = IO(
                    out = PrintStream(out)
                )
            )
            interpreter.eval(
                """
                var n = 5000
                var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))
                var pi = 4 * reduce(sequence, 0, x y -> x + y)
                print "pi = "
                out pi
                """.trimIndent().byteInputStream()
            )
            assertEquals("pi = 3.14179261348960", String(out.toByteArray()))
        }
    }

    @Test
    fun `should report evaluation errors to err`() {
        ByteArrayOutputStream().use { err ->
            val interpreter = Interpreter(
                io = IO(
                    err = PrintStream(err)
                )
            )
            interpreter.eval(
                """
                var a = 1
                out b
                """.trimIndent().byteInputStream()
            )
            assertEquals(
                """
                Unknown variable 'b' at 2:4
                """.trimIndent(),
                String(err.toByteArray()).trim()
            )
        }
    }

    @Test
    fun `should report syntax errors to err`() {
        ByteArrayOutputStream().use { err ->
            val interpreter = Interpreter(
                io = IO(
                    err = PrintStream(err)
                )
            )
            interpreter.eval(
                """
                wrong content
                """.trimIndent().byteInputStream()
            )
            assertEquals(
                """
                Syntax errors found:
                line 1:0 mismatched input 'wrong' expecting {'var', 'out', 'print'}
                """.trimIndent(),
                String(err.toByteArray()).trim()
            )
        }
    }

}