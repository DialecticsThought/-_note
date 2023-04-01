package com.itheima.a11;
import java.io.IOException;
import java.lang.reflect.Proxy;

public class JdkProxyDemo {

    interface Foo {
        void foo();
    }

    static final class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }
    }

    // jdk 只能针对接口代理  代理类和被代理类是兄弟关系 不能相互类型转换
    // cglib
    public static void main(String[] param) throws IOException {
        // 目标对象
        Target target = new Target();
        /*
        * 代理类没有源码 直接在运行期间生成字节码 字节码要被类加载器加载才能运行
        * */
        ClassLoader loader = JdkProxyDemo.class.getClassLoader(); // 用来加载在运行期间动态生成的字节码
        /*
        * 类加载器
        * 实现的接口类型（数组）
        * 实现接口的抽象方法 也就是invocationHandler
        * */
        Foo proxy = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, (p, method, args) -> {
            System.out.println("before...");//相当于前置增强
            // 目标.方法(参数)
            // 方法.invoke(目标, 参数);
            Object result = method.invoke(target, args);
            System.out.println("after....");//相当于后置增强
            return result; // 让代理也返回目标方法执行的结果
        });

        System.out.println(proxy.getClass());
        //代理对象的foo方法执行的是invocationHandler
        proxy.foo();

        System.in.read();
    }
}
