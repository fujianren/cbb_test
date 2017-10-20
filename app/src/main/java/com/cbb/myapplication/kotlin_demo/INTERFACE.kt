package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class INTERFACE {

    interface MyInterface{
        fun bar()

        fun boo(){
            // 可选的方法体
            println("foo")
        }
    }


    class Child: MyInterface {
        override fun bar() {
            // 方法体
            println("bar")
        }
    }


    interface MyInterface1{
        var name:String
    }

    class MyImp:MyInterface1 {
        override var name: String = "runboo"

    }
}