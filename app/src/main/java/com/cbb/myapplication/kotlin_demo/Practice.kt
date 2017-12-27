package com.cbb.myapplication.kotlin_demo

import com.cbb.myapplication.kotlin_demo.Practice.Person

/**
 * @author   chenbb
 * @create   2017/12/27
 * @desc
 */

fun main(args: Array<String>) {
    var person = Person()
    println("${person.name}年龄${person.age}")
    person.age = 24
}

class Practice{

    class Person{
        var name = "小芳"
        var age = 28

        get() = 18

        // set是可以私有化的，这样外部是无法修改其属性的
        set(value) {
            field = value
            if (value > 18){
                println("${name}年龄${age},是成年人")
            } else {
                println("${name}年龄${age},是未成年人")
            }
        }
    }


}