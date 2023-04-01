package com.itheima.a12;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class A12 {

    interface Foo {
        void foo();
        int bar();
    }

    static class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }

        @Override
        public int bar() {
            System.out.println("target bar");
            return 100;
        }
    }
        /*
        * 定义一个接口  成为自己写的$Proxy0 一个成员变量 所以在new $Proxy0（） 的时候
        * 传入这个接口的实现类
        * $Proxy0在重写被代理类的foo方法的时候里面执行的是这个接口的实现
        * */
    /*interface InvocationHandler {
    //Object是因为返回值有各种各样的类型
        Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    }*/

    public static void main(String[] param) {
        // ⬇️1. 创建代理，这时传入 InvocationHandler
        Foo proxy = new $Proxy0(new InvocationHandler() {
            // ⬇️5. 进入 InvocationHandler
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                // ⬇️6. 功能增强
                System.out.println("before...");
                // ⬇️7. 反射调用目标方法
                //new Target().foo();
                return method.invoke(new Target(), args);
            }
        });
        // ⬇️2. 调用代理方法
        proxy.foo();
        proxy.bar();
        /*
            学到了什么: 代理一点都不难, 无非就是利用了多态、反射的知识
                1. 方法重写可以增强逻辑, 只不过这【增强逻辑】千变万化, 不能写死在代理内部
                2. 通过接口回调将【增强逻辑】置于代理类之外
                3. 配合接口方法反射(也是多态), 就可以再联动调用目标方法
         */
    }
}
