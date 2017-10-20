package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class EnumDemo {

    enum class Color(val rgb: Int) {
        // 初始化
        RED(0xff0000),
        GREEN(0x00ff00),
        BLUE(0x0000ff)
    }

    enum class Color1 {
        RED, BLACK, BLUE, GREEN, WHITE
    }


    fun main(args: Array<String>) {
        var color: Color1 = Color1.BLUE
        println(Color1.values())
        println(Color1.valueOf("RED"))
        println(color.name)
        println(color.ordinal)


        printAllValues<RGB>()   // 输出 RED, GREEN, BLUE
    }


    enum class ProtocolState {
        WATING {
            override fun signal() = TALKING
        },

        TALKING {
            override fun signal() = WATING
        };

        abstract fun signal(): ProtocolState
    }

    enum class RGB{RED, GREEN, BLUE}

    inline fun <reified T: Enum<T>> printAllValues(){
        print(enumValues<T>().joinToString { it.name })
    }



    enum class Shape(value: Int){
        ovel(100),
        rectangle(200)
    }

}