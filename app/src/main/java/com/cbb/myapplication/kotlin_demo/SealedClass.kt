package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc    密封类
 *
 * 密封类 的一个子类可以有可包含状态的多个实例
 * 所有的子类都必须要内嵌在密封类中
 */
sealed class SealedClass {

    data class Const(val number: Double) : SealedClass()

    data class Sum(val e1: SealedClass, val e2: SealedClass) : SealedClass()

    object NotANumber : SealedClass()

    fun eval(expr: SealedClass): Double = when (expr) {
        is Const -> expr.number
        is Sum -> eval(expr.e1) + eval(expr.e2)
        NotANumber -> Double.NaN
    }
}