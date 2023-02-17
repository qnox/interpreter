package me.qnox.interpreter

import java.math.MathContext
import java.math.RoundingMode

class EvaluationContext(vararg variables: Pair<String, Any>) {

    val mathContext = MathContext(10, RoundingMode.HALF_UP)

    private val variables = mutableMapOf<String, Any>(*variables)
    fun assign(identifier: String, value: Any) {
        variables[identifier] = value
    }

    fun getValueOrElse(identifier: String, defaultValue: () -> Any): Any {
        return variables.getOrElse(identifier, defaultValue)
    }

}
