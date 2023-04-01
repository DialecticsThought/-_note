package org.springframework.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/*
    模拟调用链过程, 是一个简单的递归过程
        1. proceed() 方法调用链中下一个环绕通知
        2. 每个环绕通知内部继续调用 proceed()
        3. 调用到没有更多通知了, 就调用目标方法
        调用类对象就是把目标和环绕通知组合在一起
 */
public class A18_1 {

    static class Target {
        public void foo() {
            System.out.println("Target.foo()");
        }
    }
    //MethodInterceptor 就是环绕通知的意思
    static class Advice1 implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice1.before()");
            Object result = invocation.proceed();// 调用下一个通知或目标
            System.out.println("Advice1.after()");
            return result;
        }
    }

    static class Advice2 implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice2.before()");
            Object result = invocation.proceed();// 调用下一个通知或目标
            System.out.println("Advice2.after()");
            return result;
        }
    }

    //定义一个调用类对象
    static class MyInvocation implements MethodInvocation {
        private Object target;  // 目标对象 1次
        private Method method; //目标方法
        private Object[] args; //目标方法的参数
        //通知的集合
        List<MethodInterceptor> methodInterceptorList; // 2次
        private int count = 1; // 调用次数

        public MyInvocation(Object target, Method method, Object[] args, List<MethodInterceptor> methodInterceptorList) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.methodInterceptorList = methodInterceptorList;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArguments() {
            return args;
        }

        @Override
        public Object proceed() throws Throwable { // 调用每一个环绕通知, 并且调用目标方法
            if (count > methodInterceptorList.size()) {//说明没有通知可以调用了
                // 通过反射调用目标， 返回并结束递归
                return method.invoke(target, args);
            }
            // 逐一调用通知, 通知执行完后count + 1
            //根据索引获取通知对象
            MethodInterceptor methodInterceptor = methodInterceptorList.get(count++ - 1);
            //
            /*
            * 执行环绕通知对象的方法
            * 因为环绕通知对象的invoke方法里面有invocation.proceed()
            * 所以本质上是多个递归调用
            * */
            return methodInterceptor.invoke(this);
        }
        //返回目标对象
        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }
    }

    public static void main(String[] args) throws Throwable {
        Target target = new Target();
        List<MethodInterceptor> list = List.of(
                new Advice1(),
                new Advice2()
        );
        MyInvocation invocation = new MyInvocation(target, Target.class.getMethod("foo"), new Object[0], list);
        invocation.proceed();
    }
}
