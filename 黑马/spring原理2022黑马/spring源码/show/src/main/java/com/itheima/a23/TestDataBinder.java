package com.itheima.a23;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.util.Date;

public class TestDataBinder {

    public static void main(String[] args) {
        // 执行数据绑定
        MyBean target = new MyBean();
        //默认水利用get set方法赋值
        DataBinder dataBinder = new DataBinder(target);
        //想要用私有成员变量赋值
        dataBinder.initDirectFieldAccess();

        MutablePropertyValues pvs = new MutablePropertyValues();
        //给属性赋值  赋值的过程中 发现 值和属性类型不一致的时候会类型转换
        pvs.add("a", "10");
        pvs.add("b", "hello");
        pvs.add("c", "1999/03/04");
        dataBinder.bind(pvs);
        System.out.println(target);
    }

    static class MyBean {
        private int a;
        private String b;
        private Date c;

        @Override
        public String toString() {
            return "MyBean{" +
                   "a=" + a +
                   ", b='" + b + '\'' +
                   ", c=" + c +
                   '}';
        }
    }
}
