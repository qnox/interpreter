package me.qnox.interpreter

import me.qnox.interpreter.parser.Parser
import me.qnox.interpreter.parser.ParsingException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ParserTest {

    private val parser = Parser()

    @Test
    fun `should successfully parse`() {
        val program = parser.parse(
            """
            var a = 2
            print "a"
            out a
            """.trimIndent().byteInputStream()
        )
        assertNotNull(program)
    }

    @Test
    fun `should report parsing errors`() {
        val ex = assertThrows<ParsingException> {
            parser.parse(
                """
                var a = (2
                var b 2  
                """.trimIndent().byteInputStream()
            )
        }
        assertEquals(
            """
            Syntax errors found:
            line 2:0 missing ')' at 'var'
            line 2:6 missing '=' at '2'
            """.trimIndent(),
            ex.message
        )
    }
}