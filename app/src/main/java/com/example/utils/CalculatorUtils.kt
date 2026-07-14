package com.example.utils

import kotlin.math.*

/**
 * Thread-safe math expression evaluator using shunting-yard algorithm.
 * Handles: +, -, × (*), ÷ (/), ^, %, sin, cos, tan, log, ln, sqrt, √, π
 * Gracefully handles incomplete expressions (e.g. "5+") by returning the partial value.
 */
object CalculatorUtils {

    fun evaluateExpression(expression: String): Double {
        if (expression.isBlank()) return 0.0
        val cleaned = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "(${PI})")
            .replace("%", "/100")
        return evaluate(cleaned)
    }

    private fun evaluate(expr: String): Double {
        val output = mutableListOf<Double>()
        val ops = mutableListOf<Char>()
        var i = 0
        var expectUnary = true

        fun applyOp() {
            if (ops.isEmpty() || output.isEmpty()) return
            val op = ops.last()
            val needsTwo = op !in "usctlnr"
            if (needsTwo && output.size < 2) {
                // Binary op with insufficient operands — discard it
                ops.removeLast()
                return
            }
            if (!needsTwo && output.size < 1) {
                // Unary op with no operand — discard it
                ops.removeLast()
                return
            }
            ops.removeLast()
            when (op) {
                'u' -> { val a = output.removeLast(); output.add(-a) }
                's' -> { val a = output.removeLast(); output.add(sin(Math.toRadians(a))) }
                'c' -> { val a = output.removeLast(); output.add(cos(Math.toRadians(a))) }
                't' -> { val a = output.removeLast(); output.add(tan(Math.toRadians(a))) }
                'l' -> { val a = output.removeLast(); output.add(log10(a)) }
                'n' -> { val a = output.removeLast(); output.add(ln(a)) }
                'r' -> { val a = output.removeLast(); output.add(sqrt(a)) }
                else -> {
                    val b = output.removeLast()
                    val a = output.removeLast()
                    val result = when (op) {
                        '+' -> a + b
                        '-' -> a - b
                        '*' -> a * b
                        '/' -> if (b == 0.0) throw ArithmeticException("Division by zero") else a / b
                        '^' -> a.pow(b)
                        else -> return
                    }
                    output.add(result)
                }
            }
        }

        fun precedence(op: Char): Int = when (op) {
            '+', '-' -> 1
            '*', '/' -> 2
            '^' -> 3
            'u', 's', 'c', 't', 'l', 'n', 'r' -> 4
            else -> 0
        }

        while (i < expr.length) {
            val ch = expr[i]

            when {
                ch == ' ' -> i++
                ch == '(' -> {
                    ops.add(ch)
                    expectUnary = true
                    i++
                }
                ch == ')' -> {
                    while (ops.isNotEmpty() && ops.last() != '(') applyOp()
                    if (ops.isNotEmpty() && ops.last() == '(') ops.removeLast()
                    expectUnary = false
                    i++
                }
                ch == '-' && expectUnary -> {
                    ops.add('u')
                    i++
                }
                ch == '+' && expectUnary -> {
                    i++ // skip unary plus
                }
                ch in '0'..'9' || ch == '.' -> {
                    val start = i
                    while (i < expr.length && (expr[i] in '0'..'9' || expr[i] == '.')) i++
                    val numStr = expr.substring(start, i)
                    output.add(numStr.toDouble())
                    expectUnary = false
                }
                ch in "+-*/^" -> {
                    while (ops.isNotEmpty() && ops.last() != '(' && precedence(ops.last()) >= precedence(ch)) applyOp()
                    ops.add(ch)
                    expectUnary = true
                    i++
                }
                ch in 'a'..'z' || ch == '√' -> {
                    val start = i
                    while (i < expr.length && (expr[i] in 'a'..'z' || expr[i] == '√')) i++
                    val func = expr.substring(start, i)
                    val mapped = when (func) {
                        "sin" -> 's'; "cos" -> 'c'; "tan" -> 't'
                        "log" -> 'l'; "ln" -> 'n'; "sqrt", "√" -> 'r'
                        else -> throw IllegalArgumentException("Unknown function: $func")
                    }
                    ops.add(mapped)
                    expectUnary = true
                }
                else -> i++ // skip unrecognized
            }
        }

        while (ops.isNotEmpty()) applyOp()

        return if (output.isEmpty()) 0.0 else output.last()
    }
}
