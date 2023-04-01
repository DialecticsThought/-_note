package com.itheima.a13;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class Proxy extends Target {
    //这个成员变量是用来当做回调的接口
    private MethodInterceptor methodInterceptor;

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
    /*
    * 定义静态成员变量
    * 通过反射获取对应的方法
    * */
    static Method save0;
    static Method save1;
    static Method save2;
    static MethodProxy save0Proxy;
    static MethodProxy save1Proxy;
    static MethodProxy save2Proxy;
    static {
        try {
            save0 = Target.class.getMethod("save");
            save1 = Target.class.getMethod("save", int.class);
            save2 = Target.class.getMethod("save", long.class);
            /*
            * methodProxy怎么创建
            * 需要传入5个类型
            * 目标类   代理类  ()代表无参V代表void  带有增强功能的代理方法名  原始方法名
            * 创建完methodProxy后
            * 通过proxy.setMethodInterceptor方法使用methodProxy 实现无反射的调用
            *
            * MethodProxy.create调用后会生成FastClass的子类 可以看ProxyFastClass 这个类是模拟
            * 这个类可以避免方法的反射调用 因为反射浪费性能
            * MethodProxy.create方法传入的参数就是FastClass类getIndex方法所用到的signature
            * */
            save0Proxy = MethodProxy.create(Target.class, Proxy.class, "()V", "save", "saveSuper");
            save1Proxy = MethodProxy.create(Target.class, Proxy.class, "(I)V", "save", "saveSuper");
            save2Proxy = MethodProxy.create(Target.class, Proxy.class, "(J)V", "save", "saveSuper");
        } catch (NoSuchMethodException e) {
            //包装成运行时异常并抛出
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 带原始功能的方法
    public void saveSuper() {
        super.save();
    }//因为cglib是原始类和代理类父子关系  调用了父类方法就是原始方法
    public void saveSuper(int i) {
        super.save(i);
    }//因为cglib是原始类和代理类父子关系  调用了父类方法就是原始方法
    public void saveSuper(long j) {
        super.save(j);
    }//因为cglib是原始类和代理类父子关系  调用了父类方法就是原始方法
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 带增强功能的方法
    @Override
    public void save() {
        try {
            methodInterceptor.intercept(this, save0, new Object[0], save0Proxy);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public void save(int i) {
        try {
            methodInterceptor.intercept(this, save1, new Object[]{i}, save1Proxy);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public void save(long j) {
        try {
            methodInterceptor.intercept(this, save2, new Object[]{j}, save2Proxy);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
