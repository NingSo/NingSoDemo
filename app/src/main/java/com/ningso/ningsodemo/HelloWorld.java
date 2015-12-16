package com.ningso.ningsodemo;

/**
 * Created by NingSo on 15/12/16.下午3:27
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class HelloWorld {

    public native String sayHello(String name); // 1.声明这是一个native函数，由本地代码实现

    static {
        System.loadLibrary("hello");   // 2.加载实现了native函数的动态库，只需要写动态库的名字
    }
}
