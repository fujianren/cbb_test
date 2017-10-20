package com.cbb.myapplication.kotlin_demo

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class GenericesDemo {

    /*============ 泛型约束 ==============*/
    fun <T> cloneWhenGreater(list: List<T>, threshold: T): List<T>{

//        return list.filter(it > threshold).map(it.clone)
        return list
    }

    /**
     * 返回
     */
    fun <T: Comparable<T>> sort(list: List<T>){

    }


    class Runoob<out A>(val a: A){
        fun foo(): A{
            return a
        }
    }

}