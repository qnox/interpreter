package me.qnox.interpreter

import me.qnox.interpreter.evaluator.EvaluationContext
import me.qnox.interpreter.evaluator.EvaluationException
import me.qnox.interpreter.evaluator.ExpressionEvaluator
import me.qnox.interpreter.parser.antlr4.LangLexer
import me.qnox.interpreter.parser.antlr4.LangParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ExpressionEvaluatorTest {

    private val expressionEvaluator = ExpressionEvaluator()

    @Test
    fun `should add two numbers`() {
        assertEquals(BigDecimal(2), eval("1.5+0.5").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `should subtract two numbers`() {
        assertEquals(BigDecimal(1), eval("1.5-0.5").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `parenthesis expression should have top priority`() {
        assertEquals(BigDecimal(4), eval("2*(1+1)").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `should multiply two numbers`() {
        assertEquals(BigDecimal(0.75), eval("1.5*0.5").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `multiplication should precede addition`() {
        assertEquals(BigDecimal(1.75), eval("1 + 1.5 * 0.5").stripTrailingZerosIfBigDecimal())
        assertEquals(BigDecimal(0.25), eval("1 - 1.5 * 0.5").stripTrailingZerosIfBigDecimal())
        assertEquals(BigDecimal(1.75), eval("1.5 * 0.5 + 1").stripTrailingZerosIfBigDecimal())
        assertEquals(BigDecimal(-0.25), eval("1.5 * 0.5 - 1").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `should divide two numbers`() {
        assertEquals(BigDecimal(3), eval("1.5/0.5").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `should exponentiate numbers`() {
        assertEquals(BigDecimal(2), eval("4 ^ 0.5").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `should exponentiate negative numbers`() {
        assertEquals(BigDecimal(-8), eval("-2 ^ 3").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `exponentiation should have max priority`() {
        assertEquals(BigDecimal(9), eval("1 + 2 ^ 3").stripTrailingZerosIfBigDecimal())
        assertEquals(BigDecimal(1), eval("9 - 2 ^ 3").stripTrailingZerosIfBigDecimal())
        assertEquals(BigDecimal(16), eval("2 * 2 ^ 3").stripTrailingZerosIfBigDecimal())
        assertEquals(BigDecimal(1), eval("8 / 2 ^ 3").stripTrailingZerosIfBigDecimal())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `should return integers in range`() {
        val seq = eval("{-1 , 1}") as Stream<BigDecimal>
        assertContentEquals(listOf(BigDecimal(-1), BigDecimal(0), BigDecimal(1)), seq.toList())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `should return mapped integers in range`() {
        val seq = eval("map({-1 , 1}, i -> i * 2)") as Stream<BigDecimal>
        assertContentEquals(listOf(BigDecimal(-2), BigDecimal(0), BigDecimal(2)), seq.toList())
    }

    @Test
    fun `should reduce integers in range`() {
        assertEquals(BigDecimal(2), eval("reduce({-1 , 2}, -4, acc i -> acc + i)").stripTrailingZerosIfBigDecimal())
    }

    @Test
    fun `should report type mismatch error`() {
        val ex = assertThrows<EvaluationException> {
            eval("{-1, 2} + 1")
        }
        assertEquals("Expected number but was sequence at 1:0", ex.message)
    }

    private fun eval(input: String, variables: List<Pair<String, Any>> = emptyList()): Any {
        val parser = createParser(input)
        val expr = parser.expr()
        return expressionEvaluator.evaluate(expr, EvaluationContext(mapOf(*variables.toTypedArray())))
    }

    private fun createParser(input: String): LangParser {
        val lexer = LangLexer(CharStreams.fromString(input))
        return LangParser(CommonTokenStream(lexer))
    }

    private fun Any?.stripTrailingZerosIfBigDecimal() = if (this is BigDecimal) {
        this.stripTrailingZeros()
    } else {
        this
    }

}
