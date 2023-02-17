package me.qnox.interpreter

import langParser
import org.antlr.v4.runtime.tree.*

class InterpreterVisitor(
    private val expressionEvaluator: ExpressionEvaluator = ExpressionEvaluator(),
    private val evaluationContext: EvaluationContext = EvaluationContext(),
    private val io: IO = IO()
) : ParseTreeVisitor<Any> {

    override fun visit(tree: ParseTree): Any? {
        return tree.accept(this)
    }

    override fun visitChildren(node: RuleNode): Any {
        return when (node) {
            is langParser.ProgramContext -> {
                node.children?.forEach { it.accept(this) }
                Unit
            }

            is langParser.StmtContext -> {
                node.children?.forEach { it.accept(this) }
                Unit
            }

            is langParser.PrintContext -> {
                val quotedString = node.quotedString.text
                io.out.print(quotedString.substring(1, quotedString.length - 1))
            }

            is langParser.OutContext -> {
                val result = node.value.accept(this)
                io.out.print(result)
            }

            is langParser.ExprContext -> {
                expressionEvaluator.evaluate(node, evaluationContext)
            }

            is langParser.AssignmentContext -> {
                evaluationContext.assign(node.variable.text, node.value.accept(this))
            }

            else -> {
                reportError(node, "Unexpected node ${node::class}")
            }
        }
    }

    override fun visitErrorNode(node: ErrorNode) {
    }

    override fun visitTerminal(node: TerminalNode) {
    }
}