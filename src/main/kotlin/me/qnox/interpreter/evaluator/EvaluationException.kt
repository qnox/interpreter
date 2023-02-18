package me.qnox.interpreter.evaluator

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.RuleNode

class EvaluationException(val context: RuleNode, message: String) : RuntimeException(message)

fun reportError(context: RuleNode, errorMessage: String): Nothing {
    throw EvaluationException(
        context,
        "$errorMessage at ${location(context)}"
    )
}

private fun location(context: RuleNode) = if (context is ParserRuleContext) {
        "${context.start.line}:${context.start.charPositionInLine}"
    } else {
        "<unknown>"
    }
