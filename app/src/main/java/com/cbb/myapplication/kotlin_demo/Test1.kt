package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 * 分发接收者虚拟解析
 * 扩展接收者静态解析
 */
class Test1 {

    open class D{}

    class D1: D(){}

    open class C {
        open fun D.foo(){
            println("D.foo in C")
        }

        open fun D1.foo(){
            println("D1.foo in C")
        }

        fun caller(d: D){
            d.foo()         // 调用扩展函数
        }

        open fun ooo(){}
    }

    class C1 : C() {
        // 扩展函数不能用super继承
        override fun D.foo() {
            println("D.foo in C1")
        }

        override fun D1.foo(){
            println("D1.foo in C1")
        }
    }

    fun main(args: Array<String>){
        C().caller(D())     // 输出 "D.foo in C"
        C1().caller(D())    // 输出 "D.foo in C1" —— 分发接收者虚拟解析
        C().caller(D1())    // 输出 "D.foo in C" —— 扩展接收者静态解析
    }
}