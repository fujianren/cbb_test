package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/18
 * @desc
 */
class BaseDataType {

    fun main(args: Array<String>) {
        val a: Int = 10000
        println(a == a)     // true，值相等，对象地址相等

        val boxedA: Int? = a
        val anotherBoxed: Int? = a

        // 三个等号 === 表示比较对象地址，两个 == 表示比较两个值大小。
        println(boxedA === anotherBoxed)     //  false，值相等，对象地址不一样
        println(boxedA == anotherBoxed)     // true，值相等
    }

    fun writ() {
        val oneMillion = 1_000_000
        val ocreditCardNumber = 1234_5678_9012_3245L        // 长整形
        val socialSecurityNumber = 999_99_9999L
        val hexBytes = 0xFF_EC_5E
        val bytes = 0b11010010_01101001_10010100_10010010

        bytes.toByte();
        bytes.toInt();
        bytes.toShort();
        bytes.toFloat();
        bytes.toDouble();
        bytes.toChar();
        bytes.toLong();

        bytes.shl(0b001)    // 左位移
        bytes.shr(0b001)    // 右位移
        bytes.ushr(0b001)   // 无符号右位移
        bytes.and(0b001)    // 与
        bytes.or(0b001)     // 或
        bytes.xor(0b001)    // 异或
        bytes.inv()     // 反向
    }

    fun funciton1() {
        // [1, 2, 3]
        val a = arrayOf(1, 2, 3)

        // [0, 2, 4]
        val b = Array(3, { i -> (i * 2)})

        val x: IntArray = intArrayOf(1, 2, 3)
        x[0] = x[1] + x[2]

    }

    fun function2(){
        var str = "abc   ndd"
        for (c in str){
            println(c)
        }

        // 删除多余的空白。
        str.trimMargin()


        val s = "runoob"
        val str2 = "$s.lenght is ${s.length}"   // 求值结果为 "runoob.length is 6"
    }




    fun decimalDigitValue(c: Char): Int {
        if (c !in '0'..'9')
            throw IllegalArgumentException("Out of range")
        return c.toInt() - '0'.toInt()      // 显式转换为数字
    }


}