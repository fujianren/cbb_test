package com.cbb.myapplication.kotlin_demo

import kotlin.reflect.KProperty

/**
 * @author   chenbb
 * @create   2017/10/19
 * @desc
 */
class TrustDemo {
    /* 委托的接口 */
    interface Base{
        fun print()
    }
    
    /* 实现此接口的被委托类 */
    class BaseImp(val x: Int): Base{
        override fun print() {
            print(x)
        }
    }

    /* 委托类 */
    class Derived(b: Base): Base by b


    fun main(args: Array<String>){
        val b = BaseImp(10)
        Derived(b).print()      // 输出10


        val e = Example()
        println(e.p)    // 访问该属性，调用 getValue() 函数

        e.p = "Runoob"  // 调用setValue() 函数
        println(e.p)

        // 延迟属性 Lazy
        println(lazyValue)   // 第一次执行，执行两次输出表达式,===computed!,Hello
        println(lazyValue)   // 第二次执行，只输出返回值,===Hello



        val user = User()
        user.name = "第一次赋值"    // 旧值：初始值 -> 新值：第一次赋值
        user.name = "第二次赋值"    // 旧值：第一次赋值 -> 新值：第二次赋值
    }


    /* 属性委托 */
    // val/var <属性名>: <类型> by <表达式>

    /* 委托的属性 */
    class Example{
        var p: String by Delegates()
    }

    /* 被委托的类 */
    class Delegates{
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String{
            return "$thisRef, 这里委托了 ${property.name} 属性"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String){
            println("$thisRef 的 ${property.name} 属性赋值为 $value")
        }
    }

    /*============ 标准委托 ==============*/
    val lazyValue: String by lazy {
        println("computed!")    // 第一次调用输出，第二次调用不执行
        "Hello"
    }


    /*
    * --------------------------------------------
    *  可观察属性Observable
    * --------------------------------------------
    */
    class User{
        var name: String by kotlin.properties.Delegates.observable("初始值"){
            property, oldValue, newValue -> println("旧值：$oldValue -> 新值：$newValue")
        }
    }



    class Site(val map: Map<String, Any?>){
        val name: String by map
        val url: String by map
    }

    fun function1(){
        // 构造函数接受一个映射参数
        val site = Site(mapOf(
                "name" to "菜鸟",
                "url" to "www.00xx.com"
        ))

        // 读取映射值
        println(site.name)
        println(site.url)
    }


    /*============ 非空 ==============*/
    class Foo{
        var notNullBar: String by kotlin.properties.Delegates.notNull<String>()
    }
    fun function2(){
        val foo = Foo()
        foo.notNullBar = "bar"
        println(foo.notNullBar)
    }

    fun example(computeFoo:() -> Foo) {
        val memoizedFoo by lazy(computeFoo)
//        if (someCondition && memoizedFoo.isValid()) {
//            memoizedFoo.doSomething()
//        }
    }


    /*
    * --------------------------------------------
    *  最后的呆萌
    * --------------------------------------------
    */
    class ResourceID(){
        val image_id: String = "101"
        val text_id: String = "102"
    }

//    class MyUI{
//        val image by bindResource(ResourceID())
//        val text by bindResource(ResourceID())
//    }
//
//    class DellImpl(d: ResourceID) : ReadOnlyProperty{
//        val id: ResourceID = d
//        override fun getValue(thisRef: MyUI, property: KProperty<*>): String{
//            if (property.name.equals("image"))
//                return property.name + "==" + id.image_id
//            else
//                return property.name + "==" + id.text_id
//        }
//
//    }
//
//    class ResourceLoader(id: ResourceID){
//        val d: ResourceID = id
//        operator fun provideDelegate(thisRef: MyUI, property: KProperty<*>): ReadOnlyProperty<MyUI, KProperty<*>>{
//            if (checkProperty(thisRef, property.name)){
//                return DellImpl(d)
//            } else {
//                throw Exception("Error ${property.name}")
//            }
//        }
//
//        private fun checkProperty(thisRef: MyUI, name: String): Boolean {
//            if (name.equals("image") || name.equals("text")){
//                return true
//            } else {
//                return false
//            }
//        }
//    }
//
//    fun bindResource(id: ResourceID) : ResourceLoader{
//        var res = ResourceLoader(id)
//        return res
//    }

}

