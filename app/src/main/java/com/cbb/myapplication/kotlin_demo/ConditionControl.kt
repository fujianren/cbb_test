package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class ConditionControl {
    val a = 3
    val b = 5

    fun tradition(){
        // 传统用法
        var max = a
        if (a < b) max = b

        // else用法
        var max1: Int
        if (a > b) {
            max = a
        } else {
            max = b
        }

        // 表达式写法
        val max2 = if (a > b) a else b

        val max3 = if (a > b){
            print("Choose a")
            a
        } else {
            print("Choose b")
            b
        }

    }

    fun main(args: Array<String>) {
        var x = 0
        if (x > 0){
            println("x大于0")
        } else if (x == 0){
            println("x等于0")
        } else {
            println("x小于0")
        }

        val c = if (a >= b) a else b
        println("c的值为$c")

    }


    fun function1() {
        val x = 5
        val y = 9
        if (x in 1..8){
            println("x 在区间内")
        }


        // when 类似其他语言的 switch 操作符
        when(x){
            1 -> print("x == 1")
            2 -> print("x == 2")
            else -> {   // else 同 switch 的 default
                print("x不是1，也不是2")
            }
        }

        when(x){
            0, 1 -> print("x == 0 or x == 1")
            else -> print("otherwise")
        }

//        val validNumbers =
        when(x){
            in 1..10 -> print("x is in the range")
//            in validNumbers -> print("x is valid")
            !in 10..20 -> print("x is outside the range")
            else -> print("none of the above")
        }


    }


    fun hasPrefix(x: Any) = when(x){
        is String -> x.startsWith("prefix")
        else -> false
    }

    fun function2() {
        val items = setOf("apple", "banana", "kiwi")
        when{
            "orange" in items -> println("juicy")
            "apple" in items -> println("apple is fine too")
        }
    }

}