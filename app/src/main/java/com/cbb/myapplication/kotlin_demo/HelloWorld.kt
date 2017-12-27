package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/18
 * @desc
 * 1.变量要么申明类型，要么被初始化
 * 2.try...catch不提示
 * 3
 */
class HelloWorld {


    fun main(args: Array<String>) {
        Greeter(args[0]).greet()          // 创建一个对象不用 new 关键字
        vars(1, 2, 3, 4, 5)

        // 匿名函数
        val sumLambda: (Int, Int) -> Int = { x, y -> x + y }
        println(sumLambda(1, 2));

        val arr = "aa.dd.bb.cc"
        arr.split(".")

    }


    class Greeter(val name: String) {
        fun greet() {
            println("Hello, $name")
        }

    }


    fun sum(a: Int, b: Int): Int {   // Int 参数，返回值 Int
        return a + b
    }

    fun det(a: Int, b: Int): Int {
        return a - b
    }

    fun sum1(a: Int, b: Int) = a + b

    public fun sum2(a: Int, b: Int): Int = a + b  // public 方法则必须明确写出返回类型


    fun printSumz(a: Int, b: Int): Unit {
        print(a + b)
    }

    // 如果是返回 Unit类型，则可以省略(对于public方法也是这样)：
    fun printSum(a: Int, b: Int) {
        print(a + b)

        var a = 1
        val s1 = "a is $a"
        a = 2
        // ${varName.fun()} 表示变量的方法返回值:
        val s2 = "${s1.replace("is", "was")}, but now is $a"
    }

    // 函数的变长参数可以用 vararg 关键字进行标识：
    fun vars(vararg v: Int) {
        for (vt in v) {
            print(vt)
        }

        var a: Int = 1
        val b: Int
        b = 1

        var x = 5
        x += 1

        val f: String = "dsd"

    }

    fun isnull(){

    }

    fun main2(args: Array<String>){
        if (args.size < 2) {
            print("Two integers expected")
            return
        }

        val x = parseInt(args[0])
        val y = parseInt(args[1])

        if (x != null && y != null){
            print(x * y)
        }
    }

    fun parseInt(string: String): Int?{
        // 类型后面加？表示可为空
        var age: String? = "23"
        //抛出空指针异常,字段后加!!像Java一样抛出空异常
        val args = age!!.toInt()

        // 不做处理返回null
        val ages1 = age?.toInt()
        val ages2 = age?.toInt() ?: -1      //age为空返回-1
        return args
    }

    fun getStringLength(obj: Any): Int?{
        if (obj is String){
            return obj.length
        }

        for (i in 1..5) print(i)

        for (i in 4..1) print(i)

        var a: Int = 2
        if (a in 1..10){    // 等同于 1 <= i && i <= 10
            print(a)
        }

        // 使用step指定步长
        for (i in 1..4 step 2) print(i)  // 输出“13”

        for (i in 4 downTo 1 step 2) print(i)  // 输出“42”

        for (i in 1 until 10) println(i)    // i in [1, 10) 排除了 10

        return null
    }
}