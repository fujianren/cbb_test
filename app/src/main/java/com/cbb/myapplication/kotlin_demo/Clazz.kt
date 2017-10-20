package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class Clazz {

    fun main(args: Array<String>){
        val site = Runoob("菜鸟", 10000)  // Kotlin 中没有 new 关键字

        var person: Person = Person("00")

        person.lastName = "wang"
        println("lastName:${person.lastName}")  // lastName:WANG

        person.no = 9
        println("no:${person.no}")  // no:9

        person.no = 20
        println("no:${person.no}")  // no:-1


        val demo = Outer.Nested().foo()
        println(demo)
    }

    class Runoob constructor(name: String){

        var url: String = "http://www.runoob.com"
        var country: String = "CN"
        var siteName = name

        // 主构造函数，构造时一定先执行该方法块
        init {
            println("初始化网站名：${name}")
        }

        // 次构造函数
        constructor(name: String, alexa: Int): this(name){
            println("Alexa 排名 $alexa")
        }


        fun printTest() {
            println("我是类的函数")
        }



        fun foo(){
            print("Foo")
        }
    }


    class Person constructor(firstName: String){

        // 初始化代码段使用 init 关键字作为前缀。
        init {
            System.out.print("FirstName is $firstName")
        }




        // var 属性名 [：类型] [= 属性值]
        var lastName: String = "zhang"
        get() = field.toUpperCase()     // 将变量赋值后转换为大写
        set

        var no: Int = 100
        get() = field
        set(value){
            if (value < 10){    //  // 如果传入的值小于 10 返回该值
                field = value
            } else {
                field = -1      //  // 如果传入的值大于等于 10 返回 -1
            }
        }

        var height: Float = 145.4f
        private set
    }

    open class Base{
        open fun f(){}
    }

    abstract class Derived: Base(){
        override abstract fun f()
    }


    class Outer {                  // 外部类
        private val bar: Int = 1
        class Nested {             // 嵌套类
            fun foo() = 2
        }
    }

}