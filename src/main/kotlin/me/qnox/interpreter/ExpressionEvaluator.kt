package me.qnox.interpreter

import ch.obermuhlner.math.big.BigDecimalMath.pow
import langParser
import langParser.ExprContext
import java.math.BigDecimal
import java.math.BigInteger

class ExpressionEvaluator {

    fun evaluate(node: ExprContext, evaluationContext: EvaluationContext): Any {
        return when (node) {
            is langParser.NumberExprContext -> {
                node.number.text.toBigDecimal()
            }

            is langParser.UnaryExprContext -> {
                val v = evaluate(node.v, evaluationContext).asBigDecimal(node.v)
                when (node.op.text) {
                    "-" -> v.negate()
                    else -> v
                }
            }

            is langParser.ParenthisedExprContext -> {
                evaluate(node.expression, evaluationContext)
            }

            is langParser.IdentifierExprContext -> {
                val identifier = node.identifier.text
                evaluationContext.getValueOrElse(identifier) {
                    reportError(node, "Unknown variable '$identifier'")
                }
            }

            is langParser.MapExprContext -> {
                val sequence = evaluate(node.input, evaluationContext).asSequence(node.input)
                val variable = node.v.text
                val mapContext = EvaluationContext()
                sequence.map { v ->
                    mapContext.assign(variable, v)
                    evaluate(node.op, mapContext)
                }
            }

            is langParser.ReduceExprContext -> {
                val sequence = evaluate(node.input, evaluationContext).asSequence(node.input)
                val accumulator = node.acc.text
                val value = node.v.text
                val reduceContext = EvaluationContext()
                sequence.reduce { acc, v ->
                    reduceContext.assign(accumulator, acc)
                    reduceContext.assign(value, v)
                    evaluate(node.op, reduceContext)
                }
            }

            is langParser.MulExprContext -> {
                val v1 = evaluate(node.v1, evaluationContext).asBigDecimal(node.v1)
                val v2 = evaluate(node.v2, evaluationContext).asBigDecimal(node.v2)
                evaluateBinaryOperation(v1, v2, node.op.text, evaluationContext, node)
            }

            is langParser.AddExprContext -> {
                val v1 = evaluate(node.v1, evaluationContext).asBigDecimal(node.v1)
                val v2 = evaluate(node.v2, evaluationContext).asBigDecimal(node.v2)
                evaluateBinaryOperation(v1, v2, node.op.text, evaluationContext, node)
            }

            is langParser.PowExprContext -> {
                val v1 = evaluate(node.v1, evaluationContext).asBigDecimal(node.v1)
                val v2 = evaluate(node.v2, evaluationContext).asBigDecimal(node.v2)
                evaluateBinaryOperation(v1, v2, node.op.text, evaluationContext, node)
            }

            is langParser.RangeExprContext -> {
                val from = evaluate(node.from, evaluationContext).asBigDecimal(node.from)
                val to = evaluate(node.to, evaluationContext).asBigDecimal(node.to)
                generateSequence(from.toBigInteger()) { v ->
                    v.add(BigInteger.ONE).takeIf { it <= to.toBigInteger() }
                }.map { it.toBigDecimal(mathContext = evaluationContext.mathContext) }
            }

            else -> {
                reportError(node, "Unknown node ${node::class}")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Any?.asSequence(context: ExprContext): Sequence<Any> {
        return if (this is Sequence<*>) {
            this as Sequence<Any>
        } else {
            reportTypeMismatch(context, "sequence", this)
        }
    }

    private fun Any?.asBigDecimal(context: ExprContext): BigDecimal {
        return if (this is BigDecimal) {
            this
        } else {
            reportTypeMismatch(context, "number", this)
        }
    }

    private fun reportTypeMismatch(context: ExprContext, expected: String, actual: Any?): Nothing {
        val errorMessage = "Expected $expected but was ${typeName(actual)}"
        reportError(context, errorMessage)
    }

    private fun typeName(actual: Any?) = when (actual) {
        null -> "<null>"
        is Number -> {
            "number"
        }

        is Sequence<*> -> {
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
        context: ExprContext
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