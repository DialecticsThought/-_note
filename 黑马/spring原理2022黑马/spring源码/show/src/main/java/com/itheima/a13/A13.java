package com.itheima.a13;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class A13 {

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        Target target = new Target();
        proxy.setMethodInterceptor(new MethodInterceptor() {
            @Override
            public Object intercept(Object p, Method method, Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                System.out.println("before...");
                // return method.invoke(target, args); // 反射调用
                // FastClass
                /*
                * 调用invoke和invokeSuper分别会生成Fastclass的子类（代理类） 可以避免反射
                * 用Target和TargetFastClass来模拟这两个子类
                * return methodProxy.invoke(target, args)
                * target和args会传递给FastClass的子类（可以看ProxyFastClass）的invoke方法
                * */
//                return methodProxy.invoke(target, args); // 内部无反射, 结合目标（target）用
                return methodProxy.invokeSuper(p, args); // 内部无反射, 结合代理（proxy）用
            }
        });

        proxy.save();
        proxy.save(1);
        proxy.save(2L);
        /*
        *
        * 调用MethodProxy.create（）会创建一个FastClass的子类
        * FastClass的子类能必变方法的反射调用
        * 如何避免
        * MethodProxy.create（）方法传入的参数 会变成签名Signature
        * 这些签名传入到FastClass的子类的getIndex方法中 得到方法对应的编号
        * 当调用methodProxy.invoke(target, args)的时候 会传入 目标对象和参数
        * 并且会调用FastClass的子类的invoke方法  invoke方法会收到methodProxy.invoke方法传入到的参数 并且 还会收到编号
        * 通过编号来执行对应原始的方法 方法的参数就是传入的参数
         * */
    }
}
