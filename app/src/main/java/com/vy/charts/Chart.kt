package com.vy.charts


import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder


class Chart(formula: String, private var color: Int) {

    private lateinit var formula: String
    private lateinit var expression: Expression
    private var isFormulaCorrect = false

    init {
        setFormula(formula)
    }

    fun setFormula(formula: String) {
        this.formula = formula
        try {
            expression = ExpressionBuilder(formula).variable("x").build()
        } catch (e: Exception) {
            if (formula != "") {
                isFormulaCorrect = false
                return
            }
        }
        isFormulaCorrect = true
    }

    fun getValuesOnInterval(start: Float, end: Float, step: Float)
            : Pair<ArrayList<Float>, ArrayList<Float>> {
        val valuesX = ArrayList<Float>()
        val valuesY = ArrayList<Float>()
        if (isFormulaCorrect && formula != "") {
            var x = start
            valuesX.add(x)
            while (x <= end) {
                try {
                    valuesY.add(expression.setVariable("x", x.toDouble())
                        .evaluate().toFloat())
                } catch (e: Exception) {
                    valuesY.add(Float.NaN)
                }
                x += step
                valuesX.add(x)
            }
        }
        return Pair(valuesX, valuesY)
    }

    fun getFormula(): String {
        return formula
    }

    fun getIsFormulaCorrect(): Boolean {
        return isFormulaCorrect
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun getColor(): Int {
        return color
    }
}
