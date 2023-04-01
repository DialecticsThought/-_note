package com.itheima.a04;

import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

// AutowiredAnnotationBeanPostProcessor 运行分析
public class DigInAutowired {
    public static void main(String[] args) throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("bean2", new Bean2()); // 这个方法 不会涉及创建过程,依赖注入,初始化 默认该方法加入的bean是成品
        beanFactory.registerSingleton("bean3", new Bean3());
        //这个解析器能让@Value注解生效
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver()); // @Value
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders); // ${} 的解析器

        // 1.  AutowiredAnnotationBeanPostProcessor查找哪些属性、方法加了 @Autowired注解, 这称之为 InjectionMetadata方法
        /*
        * 这个处理器 的处理过程 如下面代码一样
        * 1. 得到bean1类中那些成员变量有@Autowired修饰
        * 2. 调用 InjectionMetadata 来进行依赖注入, 注入时按类型查找值
        * */
        AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
        //后置处理器需要bean工厂 因为A依赖于B 那么找到B 就得找到B的bean工厂
        processor.setBeanFactory(beanFactory);

        Bean1 bean1 = new Bean1();
//        System.out.println(bean1);
//        processor.postProcessProperties(null, bean1, "bean1"); // 执行依赖注入 @Autowired @Value 那么让bean1 依赖的bean2注入到bean1
//        System.out.println(bean1);

//        Method findAutowiringMetadata = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
//        findAutowiringMetadata.setAccessible(true);
        //这个方法可以得到bean1类中那些成员变量有@Autowired修饰
//        InjectionMetadata metadata = (InjectionMetadata) findAutowiringMetadata.invoke(processor, "bean1", Bean1.class, null);// 获取 Bean1 上加了 @Value @Autowired 的成员变量，方法参数信息
//        System.out.println(metadata);

        // 2. 调用 InjectionMetadata 来进行依赖注入, 注入时按类型查找值
//        metadata.inject(bean1, "bean1", null);
//        System.out.println(bean1);

        // 3. 如何按类型查找值
        Field bean3 = Bean1.class.getDeclaredField("bean3");
        //false 根据成员变量的类型去找对应的对象 没有的话 也无所谓 不会报错  把成员变量的信息封装到DependencyDescriptor
        DependencyDescriptor dd1 = new DependencyDescriptor(bean3, false);
        //doResolveDependency根据成员变量信息得到相应的类型 再根据类型从容器中找符合要求的bean对象
        Object o = beanFactory.doResolveDependency(dd1, null, null, null);
        System.out.println(o);
        //@Autowired注解加在方法上的情况
        Method setBean2 = Bean1.class.getDeclaredMethod("setBean2", Bean2.class);
        DependencyDescriptor dd2 =
                //0表示需要解析该方法的第几个参数
                new DependencyDescriptor(new MethodParameter(setBean2, 0), true);
        //有了方法参数信息 doResolveDependency根据成员变量信息得到相应的类型 再根据类型从容器中找符合要求的bean对象
        Object o1 = beanFactory.doResolveDependency(dd2, null, null, null);
        System.out.println(o1);

        Method setHome = Bean1.class.getDeclaredMethod("setHome", String.class);
        DependencyDescriptor dd3 = new DependencyDescriptor(new MethodParameter(setHome, 0), true);
        Object o2 = beanFactory.doResolveDependency(dd3, null, null, null);
        System.out.println(o2);

    }
}
