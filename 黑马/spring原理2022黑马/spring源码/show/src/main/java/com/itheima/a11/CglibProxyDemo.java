package com.itheima.a11;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CglibProxyDemo {

    static class Target {
        public void foo() {
            System.out.println("target foo");
        }
    }

    // 代理是子类型, 目标是父类型  不同于jdk的兄弟关系
    public static void main(String[] param) {
//        Target target = new Target();
        //创建出的代理类是目标的子类
        /*
        * callback相当于jdk代理的invocationHandler
        * MethodInterceptor是callback的子接口
        * method指的是方法
        * args是方法的实际参数
        * p是代理对象
        * target是被代理对象
        *
        * 如果被代理类是final修饰的话 因为cglib是父子关系增强的 所以报错
        * 如果增强的方法是final修饰的话 不会被增强
        * */
        Target proxy = (Target) Enhancer.create(Target.class, (MethodInterceptor) (p, method, args, methodProxy) -> {
            System.out.println("before...");
//            Object result = method.invoke(target, args); // 用方法反射调用目标
            // methodProxy 它可以避免反射调用
//            Object result = methodProxy.invoke(target, args); // 内部没有用反射, 需要目标对象 （spring）
            Object result = methodProxy.invokeSuper(p, args); // 内部没有用反射, 需要代理对象
            System.out.println("after...");
            return result;
        });
        // 调用代理方法
        proxy.foo();

    }
}
