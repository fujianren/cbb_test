package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class Function {
    class User(var name:String)

    fun User.Print(){
        print("用户名 $name")
    }

    fun main(args: Array<String>){
        var user = User("Runoob")
        user.Print()

        val l = mutableListOf<Int>(1, 2, 3)
        l.swap(0, 2)
        println(l.toString())       // 输出[3,2,1]
    }


    fun MutableList<Int>.swap(index1: Int, index2: Int){
        val tmp = this[index1]  // this对应该列表
        this[index1] = this[index2]
        this[index2] = tmp

        var c = C()
        c.foo()


        var t = null
        println(t.toString())

        // 扩展属性
        listOf(1, 3, 4).lastIndex

        // 伴生对象
        println("no:${MyClass.no}")
        MyClass.foo()
    }

    // 若扩展函数和成员函数一致，则使用该函数时，会优先使用成员函数。
    class C{
        fun foo(){
            println("成员函数")
        }
    }

    fun C.foo(){
        println("扩展函数")
    }


    /* 通过 this 来判断接收者是否为 NULL,这样，即使接收者为 NULL,也可以调用扩展函数 */
    fun Any?.toString(): String{
        if (this == null) return "null"

        return toString()
    }

    /* 对属性进行扩展 */
    val <T> List<T>.lastIndex: Int
    get() = size - 1



    /*============ 伴生对象的扩展 ==============*/
    class MyClass{
        companion object {
        }
    }

    fun MyClass.Companion.foo(){
        println("伴生对象的扩展函数")
    }

    val MyClass.Companion.no: Int
    get() = 10

    /*
    * --------------------------------------------
    *  分发接收者虚拟解析
    *  扩展接收者静态解析
    * --------------------------------------------
    */
}