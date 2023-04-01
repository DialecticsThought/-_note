package com.itheima.a45;

import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;

@SpringBootApplication
public class A45 {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(A45.class, args);

        Bean1 proxy = context.getBean(Bean1.class);
        /*
            1.演示 spring 代理的设计特点
                依赖注入和初始化影响的是原始对象 针对原始对象的依赖注入和初始化是不会被增强的
                代理与目标是两个对象，二者成员变量并不共用数据
                依赖注入的时候没有被增强 所以代理对象一开始是没有依赖的成员变量的
                但是调用代理对象通过调用get方法间接调用到原始对象的get方法 最终获取的是原始对象所以来的对象
         */
//        showProxyAndTarget(proxy);
//        System.out.println(">>>>>>>>>>>>>>>>>>>");
//        System.out.println(proxy.getBean2());
//        System.out.println(proxy.isInitialized());

        /*
            2.演示 static 方法、final 方法、private 方法均无法增强
         */

        proxy.m1();
        proxy.m2();
        proxy.m3();
        Method m4 = Bean1.class.getDeclaredMethod("m1");
        m4.setAccessible(true);
        m4.invoke(proxy);

        context.close();
    }

    public static void showProxyAndTarget(Bean1 proxy) throws Exception {
        System.out.println(">>>>> 代理中的成员变量");
        System.out.println("\tinitialized=" + proxy.initialized);
        System.out.println("\tbean2=" + proxy.bean2);
        /*
        * Spring创建代理的时候 都会实现advise接口
        * 转成这个接口类型之后调用etTargetSource().getTarget()之后得到原始对象
        * Spring容器是不会存储原始对象的
        * */
        if (proxy instanceof Advised advised) {
            System.out.println(">>>>> 目标中的成员变量");
            Bean1 target = (Bean1) advised.getTargetSource().getTarget();
            System.out.println("\tinitialized=" + target.initialized);
            System.out.println("\tbean2=" + target.bean2);
        }
    }
}
