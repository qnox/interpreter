package me.qnox.interpreter

import java.math.MathContext
import java.math.RoundingMode

class EvaluationContext(variables: Map<String, Any> = emptyMap()) {

    val mathContext = MathContext(10, RoundingMode.HALF_UP)

    private val variables: MutableMap<String, Any> = HashMap(variables)

    fun assign(identifier: String, value: Any) {
        variables[identifier] = value
    }

    fun derive(innerVariables: Map<String, Any>): EvaluationContext {
        return EvaluationContext(variables + innerVariables)
    }

    fun getValueOrElse(identifier: String, defaultValue: () -> Any): Any {
        return variables.getOrElse(identifier, defaultValue)
    }

}
