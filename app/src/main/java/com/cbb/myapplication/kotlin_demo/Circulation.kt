package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 * for (item in collection) print(item)
 *
 * for(item: Int in ints){}
 */
class Circulation {

    fun main(args: Array<String>) {
        val items = listOf("apple", "banana", "kiwi")

        // 元素包含
        for (item in items){
            println(item)
        }

        // 在区间上遍历
        for (index in items.indices){
            println("the element at $index is ${items[index]}")
        }
    }

    fun function1() {
        println("=====while使用=====")
        var x = 5
        while (x > 0){
            println(x--)
        }


        println("====do...while使用====")
        var y = 5
        do {
            println(y--)
        } while (y > 0)
    }

    fun function2() {
        for(i in 1..10){
            if (i == 3) continue
            println(i)
            if (i > 5) break
        }
    }

    fun foo(){
        var ints = arrayOf(1, 2, 4, 5, 7)
        var it = 2
        ints.forEach {
            if (it == 0) return@forEach
            print(it)
        }

        ints.forEach lit@{
            if (it == 0) return@lit
            print(it)
        }
    }


}