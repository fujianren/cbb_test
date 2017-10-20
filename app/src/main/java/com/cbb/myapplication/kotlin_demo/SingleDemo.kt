package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/20
 * @desc     单例模式
 */
class SingleDemo {

    public class Singleton private constructor(){
        // 初始化代码块
        init {
            println("This ($this) is a singleton")
        }

        // 生成对象
        private object Holder{
            val INSTANCE = Singleton()
        }

        // 静态代码块
        companion object {
//            val instance1 = Singleton()

            val instance: Singleton by lazy {
                Holder.INSTANCE
            }
        }

        var b: String? = null
    }


    /*============ 带参数 ==============*/
    class Singleton1 private constructor(str: String){
        var string : String = str

        init {
            println("str is $str")
            println("string is $string")
        }

        companion object {
            var instance: Singleton1? = null

            fun getInstance(c: String): Singleton1{
                if (instance == null){
                    synchronized(Singleton1::class){
                        if (instance == null){
                            instance = Singleton1(c)
                        }
                    }
                }
                return instance!!
            }
        }

    }
}