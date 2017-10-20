package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class DATA {
    data class User(val name: String, val age: Int)

    fun main(args: Array<String>){
        val jack = User(name = "jake", age = 1)
        val olderJack  = jack.copy(age = 2)

        println(jack)       // User(name=Jack, age=1)
        println(olderJack)  // User(name=Jack, age=2)


        // 组件函数
        val jane = User("Jane", 35)
        val (name, age) = jane
        println("$name, $age years of age")     // prints "Jane, 35 years of age"
    }


    /*
    * --------------------------------------------
    *  密封类sealed
    * --------------------------------------------
    */
//    sealed class Expr
//
//    data class Const(val number: Double): Expr()
//    data class Sum(val e1: Expr, val e2: Expr) : Expr()
//    object NotANumber: Expr()
//
//    fun eval(expr: Expr): Double = when(expr){
//        is Const -> expr.number
//        is Sum -> eval(expr.e1) + eval(expr.e2)
//        NotANumber -> Double.NaN
//    }

}