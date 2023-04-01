package com.itheima.a12;

//import com.itheima.a13.A13.InvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public class $Proxy0 extends Proxy implements A12.Foo {
    //InvocationHandler h;
    //因为继承了Proxy父类 所以直接用h 不需要在类里面定义成员变量
    public $Proxy0(InvocationHandler h) {
        super(h);
    }

    /*
     * 定义一个接口  是自己写的$Proxy0 一个成员变量 所以在new $Proxy0（） 的时候
     * 传入这个接口的实现类
     * $Proxy0在重写被代理类的foo方法的时候里面执行的是这个接口的实现
     * */
    /*interface InvocationHandler {
    //Object是因为返回值有各种各样的类型
        Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    }*/

    static Method foo;
    static Method bar;
    // 方法对象只要获取一次 所以用静态代码块
    static {
        try {
            foo = A12.Foo.class.getMethod("foo");
            bar = A12.Foo.class.getMethod("bar");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());//转成运行时异常再抛出
        }
    }

    // ⬇️3. 进入代理方法
    @Override
    public void foo() {
        try {
            //第一个参数是代理对象 也就是自己
            // ⬇️4. 回调 InvocationHandler
            h.invoke(this, foo, new Object[0]);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    // ⬇️3. 进入代理方法
    @Override
    public int bar() {
        try {
            //第一个参数是代理对象 也就是自己
            // ⬇️4. 回调 InvocationHandler
            Object result = h.invoke(this, bar, new Object[0]);
            return (int) result;
        } catch (RuntimeException | Error e) {//运行时异常 直接抛
            throw e;
        } catch (Throwable e) {//检查异常
            throw new UndeclaredThrowableException(e);//把检查异常转变成运行时异常再抛出
        }
    }
}
