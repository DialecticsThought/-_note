package com.itheima.a23.sub;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TestGenericType {
    public static void main(String[] args) {
        // 获取泛型参数的小技巧
        // 1. java api
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        //getGenericSuperclass()获得带有泛型信息的父类
        //com.itheima.a23.sub.BaseDao<com.itheima.a23.sub.Teacher>
        Type type = TeacherDao.class.getGenericSuperclass();
        System.out.println(type);
        //进行强转
        if (type instanceof ParameterizedType parameterizedType) {
            //class com.itheima.a23.sub.Teacher
            System.out.println(parameterizedType.getActualTypeArguments()[0]);
        }

        // 2. spring api 1
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        //传入原类型 父类型
        Class<?> t = GenericTypeResolver.resolveTypeArgument(TeacherDao.class, BaseDao.class);
        //class com.itheima.a23.sub.Teacher
        System.out.println(t);

        // 3. spring api 2
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(ResolvableType.forClass(TeacherDao.class).getSuperType().getGeneric().resolve());
    }

}
