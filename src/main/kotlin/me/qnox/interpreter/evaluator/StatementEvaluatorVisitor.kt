package me.qnox.interpreter.evaluator

import me.qnox.interpreter.IO
import me.qnox.interpreter.parser.antlr4.LangParser
import org.antlr.v4.runtime.tree.*

class StatementEvaluatorVisitor(
    private val evaluationContext: EvaluationContext = EvaluationContext(),
    private val io: IO = IO()
) : ParseTreeVisitor<Any> {

    private val expressionEvaluator: ExpressionEvaluator = ExpressionEvaluator()

    override fun visit(tree: ParseTree): Any? {
        return tree.accept(this)
    }

    override fun visitChildren(node: RuleNode): Any {
        return when (node) {
            is LangParser.ProgramContext -> {
                node.children?.forEach { it.accept(this) }
                Unit
            }

            is LangParser.PrintContext -> {
                val quotedString = node.quotedString.text
                io.out.print(quotedString.substring(1, quotedString.length - 1))
            }

            is LangParser.OutContext -> {
                val result = node.value.accept(this)
                io.out.print(result)
            }

            is LangParser.ExprContext -> {
                expressionEvaluator.evaluate(node, evaluationContext)
            }

            is LangParser.AssignmentContext -> {
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