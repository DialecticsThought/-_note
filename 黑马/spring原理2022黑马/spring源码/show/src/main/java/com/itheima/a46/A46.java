package com.itheima.a46;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

// 本章节作为第四讲的延续
@Configuration
@SuppressWarnings("all")
public class A46 {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(A46.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        //得到@Value 的内容
        ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
        resolver.setBeanFactory(beanFactory);

        test1(context, resolver, Bean1.class.getDeclaredField("home"));
        // test2(context, resolver, Bean1.class.getDeclaredField("age"));
        // test3(context, resolver, Bean2.class.getDeclaredField("bean3"));
        //test3(context, resolver, Bean4.class.getDeclaredField("value"));
    }

    private static void test3(AnnotationConfigApplicationContext context, ContextAnnotationAutowireCandidateResolver resolver, Field field) {
        /*
        * 到哪一个成员变量上 方法上找@Value 所以需要一个成员变量/方法的描述
        * field 表示成员变量
        * false表示@Value括号里的值不存在也无所谓不会报错
        * */
        DependencyDescriptor dd1 = new DependencyDescriptor(field, false);
        // 1.获取 @Value 括号里 的内容 eg： @Value("${JAVA_HOME}") 的 ${JAVA_HOME}
        String value = resolver.getSuggestedValue(dd1).toString();
        System.out.println(value);

        // 2.1解析 ${} eg：解析${JAVA_HOME}得到真正的路径
        value = context.getEnvironment().resolvePlaceholders(value);
        System.out.println(value);
        System.out.println(value.getClass());

        /*
        * 2.1解析 #{}
        * 调用evaluate方法会去找#{} 这种表达式 有的话会去解析EL表达式
        * */
        Object bean3 = context.getBeanFactory().getBeanExpressionResolver().evaluate(value, new BeanExpressionContext(context.getBeanFactory(), null));

        /*
        * 3.类型转换 getTypeConverter()是高层的api 该方法只做类型转换不会数据绑定
        * 因为解析出来的是String 但是需要int类型
        * 参数一：需要转换的值
        * 参数二：需要转换成那一个类型
        * */
        Object result = context.getBeanFactory().getTypeConverter().convertIfNecessary(bean3, dd1.getDependencyType());
        System.out.println(result);
    }

    private static void test2(AnnotationConfigApplicationContext context, ContextAnnotationAutowireCandidateResolver resolver, Field field) {
        /*
         * 到哪一个成员变量上 方法上找@Value 所以需要一个成员变量/方法的描述
         * field 表示成员变量
         * false表示@Value括号里的值不存在也无所谓不会报错
         * */
        DependencyDescriptor dd1 = new DependencyDescriptor(field, false);
        // 1.获取 @Value 的内容
        String value = resolver.getSuggestedValue(dd1).toString();
        System.out.println(value);

        // 2.解析 ${}
        value = context.getEnvironment().resolvePlaceholders(value);
        System.out.println(value);
        System.out.println(value.getClass());
        /*
         * 3.类型转换 getTypeConverter()是高层的api 该方法只做类型转换不会数据绑定
         * 因为解析出来的是String 但是需要int类型
         * 参数一：需要转换的值
         * 参数二：需要转换成那一个类型
         * */
        Object age = context.getBeanFactory().getTypeConverter().convertIfNecessary(value, dd1.getDependencyType());
        System.out.println(age.getClass());
    }

    private static void test1(AnnotationConfigApplicationContext context, ContextAnnotationAutowireCandidateResolver resolver, Field field) {
        /*
         * 到哪一个成员变量上 方法上找@Value 所以需要一个成员变量/方法的描述
         * field 表示成员变量
         * false表示@Value括号里的值不存在也无所谓不会报错
         * */
        DependencyDescriptor dd1 = new DependencyDescriptor(field, false);
        // 获取 @Value 的内容
        String value = resolver.getSuggestedValue(dd1).toString();
        System.out.println(value);

        // 解析 ${}
        value = context.getEnvironment().resolvePlaceholders(value);
        System.out.println(value);
    }

    public class Bean1 {
        @Value("${JAVA_HOME}")
        private String home;
        @Value("18")
        private int age;
    }

    public class Bean2 {
        @Value("#{@bean3}") // SpringEL表达式实现@Autowire类似的依赖注入       #{SpEL}
        private Bean3 bean3;
    }

    @Component("bean3")
    public class Bean3 {
    }

    static class Bean4 {
        @Value("#{'hello, ' + '${JAVA_HOME}'}")
        private String value;
    }
}
