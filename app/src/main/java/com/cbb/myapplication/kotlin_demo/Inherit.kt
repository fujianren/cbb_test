package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 *  abstract    // 抽象类
 *  final       // 类不可继承，默认属性
 *  enum        // 枚举类
 *  open        // 类可继承，类默认是final的
 *  annotation  // 注解类
 *
 * private    // 仅在同一个文件中可见
 * protected  // 同一个文件中或子类可见
 * public     // 所有调用的地方都可见
 * internal   // 同一个模块中可见
 */
class Inherit {

    open class Person(var name: String, var age: Int){}

    class Student(name: String, age: Int, var no: String, var score: Int): Person(name, age){

    }

    open class Person1(name: String){
        /* 次级构造函数 */
        constructor(name: String, age: Int) : this(name){
             println("-------基类次级构造函数---------")
        }
    }

    class Student1 : Person{
        constructor(name: String, age: Int, no: String, score: Int) :super(name, age){
            println("=======继承类次级构造函数=======")
            println("学生名：${name}")
            println("年龄：${age}")
            println("学号：${no}")
            println("成绩：${score}")
        }

    }


    fun main(args: Array<String>){
        val s = Student("Runoob", 18, "S1234", 89)
        println("学生名：${s.name}")
        println("年龄：${s.age}")
        println("学号：${s.no}")
        println("成绩：${s.score}")
    }


    open class PP{
        // 允许子类重写该函数，那么就要手动添加 open 修饰
        open fun study(){
            println("我毕业了！！！")
        }
    }

    class SS() : PP(){
        override fun study() {
            println("我在读大学")
        }
    }


    open class A{
        open fun f(){
            print("A")
        }
        fun a(){
            print("a")
        }
    }


    interface B{
        fun f(){
            print("B")
        }
        fun b(){
            print("b")
        }
    }

    class C() : A(), B {
        override fun f() {
            super<A>.f()
            super<B>.f()
        }
    }


    /*============ 属性重写 ==============*/
    interface Foo{
        val count: Int
    }

    class Bar1: Foo {
        /* 用一个var属性重写一个val属性 */
        override var count: Int = 0
    }

    class Bar2(override val count: Int): Foo
}