package com.itheima.a22;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/*
    目标: 如何获取方法参数名, 注意把 a22 目录添加至模块的类路径
        1. a22 不在 src 是避免 idea 自动编译它下面的类
        2. spring boot 在编译时会加 -parameters
        3. 大部分 IDE 编译时都会加 -g
     在project里面添加JAR or Directory
 */
public class A22 {

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException {
        // 1. 反射获取参数名
        //执行之前 在终端执行 javac -parameters .\Bean.java  会生成参数表 可以通过反射获取
        Method foo = Bean2.class.getMethod("foo", String.class, int.class);
        /*for (Parameter parameter : foo.getParameters()) {
            System.out.println(parameter.getName());
        }*/

        // 2. 基于 LocalVariableTable 本地变量表
        // LocalVariableTableParameterNameDiscoverer通过ASM获取本地变量表
        //执行之前 javac -g .\Bean.java  会生成本地变量表  但是不能通过反射获取本地变量表
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(foo);
        System.out.println(Arrays.toString(parameterNames));

        /*
            学到了什么
                a. 如果编译时添加了 -parameters 可以生成参数表, 反射时就可以拿到参数名  接口和普通类都有效
                b. 如果编译时添加了 -g 可以生成调试信息, 但分为两种情况
                    1. 普通类, 会包含局部变量表, 用 asm 可以拿到参数名
                    2. 接口, 不会包含局部变量表, 无法获得参数名 (这也是 MyBatis 在实现 Mapper 接口时为何要提供 @Param 注解来辅助获得参数名)
                 javac -g .\Bean.java  会生成本地变量表  但是不能通过反射获取本地变量表
                 javac -parameters .\Bean.java  会生成参数表 可以通过反射
         */
    }

}
