package me.qnox.interpreter.evaluator

import ch.obermuhlner.math.big.BigDecimalMath.pow
import me.qnox.interpreter.parser.antlr4.LangParser
import java.math.BigDecimal
import java.math.BigInteger
import java.util.stream.Stream

/**
 * Calculates expressions for grammar parsed by [me.qnox.interpreter.parser.Parser] and returns the following result
 * types:
 *  * [BigDecimal] for numbers
 *  * [Stream] for sequences and ranges
 *
 * `map` and `reduce` functions are calculation is based on parallel streams.
 */
class ExpressionEvaluator {

    fun evaluate(node: LangParser.ExprContext, evaluationContext: EvaluationContext): Any {
        return when (node) {
            is LangParser.NumberExprContext -> {
                node.number.text.toBigDecimal()
            }

            is LangParser.UnaryExprContext -> {
                val v = evaluate(node.v, evaluationContext).asBigDecimal(node.v)
                when (node.op.text) {
                    "-" -> v.negate()
                    else -> v
                }
            }

            is LangParser.ParenthisedExprContext -> {
                evaluate(node.expression, evaluationContext)
            }

            is LangParser.IdentifierExprContext -> {
                val identifier = node.identifier.text
                evaluationContext.getValueOrElse(identifier) {
                    reportError(node, "Unknown variable '$identifier'")
                }
            }

            is LangParser.MapExprContext -> {
                val sequence = evaluate(node.input, evaluationContext).asSequence(node.input)
                val variable = node.v.text
                sequence.map { v ->
                    evaluate(node.op, EvaluationContext(mapOf(variable to v)))
                }
            }

            is LangParser.ReduceExprContext -> {
                val sequence = evaluate(node.input, evaluationContext).asSequence(node.input)
                val accumulator = node.acc.text
                val value = node.v.text
                sequence.reduce { acc, v ->
                    evaluate(
                        node.op,
                        EvaluationContext(
                            mapOf(
                                accumulator to acc,
                                value to v
                            )
                        )
                    )
                }.orElse(BigDecimal.ZERO)
            }

            is LangParser.BinaryExprContext -> {
                val v1 = evaluate(node.v1, evaluationContext).asBigDecimal(node.v1)
                val v2 = evaluate(node.v2, evaluationContext).asBigDecimal(node.v2)
                evaluateBinaryOperation(v1, v2, node.op.text, evaluationContext, node)
            }

            is LangParser.RangeExprContext -> {
                val from = evaluate(node.from, evaluationContext).asBigDecimal(node.from)
                val to = evaluate(node.to, evaluationContext).asBigDecimal(node.to)
                Stream.iterate(from.toBigInteger()) { v ->
                    v.add(BigInteger.ONE)
                }
                    .takeWhile { it <= to.toBigInteger() }
                    .map { it.toBigDecimal(mathContext = evaluationContext.mathContext) }
                    .parallel()
            }

            else -> {
                reportError(node, "Unknown node ${node::class}")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Any?.asSequence(context: LangParser.ExprContext): Stream<Any> {
        return if (this is Stream<*>) {
            this as Stream<Any>
        } else {
            reportTypeMismatch(context, "sequence", this)
        }
    }

    private fun Any?.asBigDecimal(context: LangParser.ExprContext): BigDecimal {
        return if (this is BigDecimal) {
            this
        } else {
            reportTypeMismatch(context, "number", this)
        }
    }

    private fun reportTypeMismatch(context: LangParser.ExprContext, expected: String, actual: Any?): Nothing {
        val errorMessage = "Expected $expected but was ${typeName(actual)}"
        reportError(context, errorMessage)
    }

    private fun typeName(actual: Any?) = when (actual) {
        null -> "<null>"
        is Number -> {
            "number"
        }

        is Stream<*> -> {
            "sequence"
        }

        else -> {
            actual::class.qualifiedName
        }
    }

    private fun evaluateBinaryOperation(
        v1: BigDecimal,
        v2: BigDecimal,
        op: String,
        evaluationContext: EvaluationContext,
        context: LangParser.ExprContext
    ) =
        when (op) {
            "+" -> v1.plus(v2)
            "-" -> v1.minus(v2)
            "*" -> v1.times(v2)
            "/" -> v1.divide(v2, evaluationContext.mathContext)
            "^" -> pow(v1, v2, evaluationContext.mathContext)
            else -> reportError(context, "Unknown binary operation $op")
        }
}