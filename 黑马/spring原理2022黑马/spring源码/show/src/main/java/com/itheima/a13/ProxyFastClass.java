package com.itheima.a13;

import org.springframework.cglib.core.Signature;

/*
* 模拟MethodProxy.create方法所生成的FastClass的子类
* 和Proxy类一起看
* */
public class ProxyFastClass {
    /*
    * 定义静态变量
    * 创建一个签名  传入 方法名 和 方法的参数
    * 传的是Proxy的方法的有关信息
    * */
    static Signature s0 = new Signature("saveSuper", "()V");
    static Signature s1 = new Signature("saveSuper", "(I)V");
    static Signature s2 = new Signature("saveSuper", "(J)V");

    /*
    * 目的： 该方法获取代理方法的编号
    * 参数1：方法名 参数2：编号
    *
        Proxy类的方法信息
            saveSuper()              0
            saveSuper(int)           1
            saveSuper(long)          2
    * MethodProxy.create调用后会生成FastClass的子类 也就是当前类
    * MethodProxy.create传入的参数就是FastClass类getIndex方法所用到的signature
    * 这个signature参数 包括方法名字、参数返回值
    *
    * 也就是说MethodProxy.create调用后会生成FastClass的子类的getIndex方法会把编号定好
    * 因为之后是通过编号来调用方法（看invoke方法）
    */
    public int getIndex(Signature signature) {
        /*
        * 判断静态变量s0 s1 s2对应哪些签名
        * 返回对应的编号
        * */
        if (s0.equals(signature)) {
            return 0;
        } else if (s1.equals(signature)) {
            return 1;
        } else if (s2.equals(signature)) {
            return 2;
        }
        return -1;
    }
    /*
    *
    * 调用 methodProxy.invokeSuper(p, args)
    * 是在proxy.setMethodInterceptor方法内部
    * 增强的功能是在.setMethodInterceptor方法内部已经实现了
    * 所以methodProxy.invokeSuper(p, args)调用的是原始功能
    *
    * */



    /*
    * 根据方法编号, 正常调用目标对象方法
    * 代理方法的编号 目标对象 参数数组
    *
    * proxy.setMethodInterceptor方法里面 会传入MethodInterceptor()的实现类
    * 这个实现类 调用methodProxy.invokeSuper(p, args);
    * p和args就会传到当前方法
    * */
    public Object invoke(int index, Object proxy, Object[] args) {
        if (index == 0) {
            ((Proxy) proxy).saveSuper();//调用目标对象的原始方法
            return null;
        } else if (index == 1) {
            //强转是因为saveSuper要一个整形数组
            //args[0]是因为只有一个参数
            ((Proxy) proxy).saveSuper((int) args[0]);
            return null;
        } else if (index == 2) {
            ((Proxy) proxy).saveSuper((long) args[0]);
            return null;
        } else {
            throw new RuntimeException("无此方法");
        }
    }

    public static void main(String[] args) {
        //FastClass起始在首次调用MethodProxy.create方法创建
        ProxyFastClass fastClass = new ProxyFastClass();
        //这一步模拟MethodProxy.create方法传入的参数 给fastClass.getIndex方法 来得到对应的编号
        int index = fastClass.getIndex(new Signature("saveSuper", "()V"));
        System.out.println(index);
        //得到编号 又因为methodProxy.invokeSuper方法传入(target, args)
        //调用fastClass.invoke方法实现不反射的调用目标方法
        fastClass.invoke(index, new Proxy(), new Object[0]);
    }
}
