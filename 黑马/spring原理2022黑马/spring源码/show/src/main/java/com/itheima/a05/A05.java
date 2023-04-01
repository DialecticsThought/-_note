package com.itheima.a05;

import com.itheima.a05.mapper.Mapper1;
import com.itheima.a05.mapper.Mapper2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/*
    BeanFactory 后处理器的作用
 */
public class A05 {
    private static final Logger log = LoggerFactory.getLogger(A05.class);

    public static void main(String[] args) throws IOException {

        // ⬇️GenericApplicationContext 是一个【干净】（没有添加任何后置处理器）的容器
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
//        context.registerBean(ConfigurationClassPostProcessor.class); // 这处理器解析 @ComponentScan @Bean @Import @ImportResource
//        context.registerBean(MapperScannerConfigurer.class, bd -> { // 这处理器解析 @MapperScanner
//            bd.getPropertyValues().add("basePackage", "com.itheima.a05.mapper");
//        });
        //注册自己定义的后置处理器
//        context.registerBean(ComponentScanPostProcessor.class); // 解析 @ComponentScan
        context.registerBean(AtBeanPostProcessor.class); // 解析 @Bean
        context.registerBean(MapperPostProcessor.class); // 解析 Mapper 接口
        //写在ComponentScanPostProcessor里面
        //从Config.class上拿到@ComponentScan注解
        ComponentScan annotation = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
        if(annotation!=null){//如果有@ComponentScan注解
            //得到@ComponentScan注解扫描到的包
            for(String p : annotation.basePackages()){
                System.out.println(p);
                //得到扫描的路径
                String path="classpath*:"+p.replace(".","/")+"/**/*.class";
                System.out.println(path);
                //用来读取类的信息一个工厂类
                CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
                AnnotationBeanNameGenerator annotationBeanNameGenerator = new AnnotationBeanNameGenerator();
                //这是ApplicationContext拓展的四大功能之一
                //得到扫描到的资源
                Resource[] resources = context.getResources(path);
                for (Resource resource: resources){
                    MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(resource);
                    System.out.println("类名："+metadataReader.getClassMetadata().getClassName());
                    /*
                    * 在扫描到的资源里面判断是否直接加了@Component注解 或者间接加了@Componen注解
                    * */
                    if(metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName())
                            ||metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName())){
                        //1.创建bean定义信息
                        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(metadataReader.getClassMetadata().getClassName()).getBeanDefinition();
                        //2.把定义信息放入到bean工厂
                        //得到bean工厂 注册bean定义信息 但是需要bean的名字 用annotationBeanNameGenerator生成
                        String beanName = annotationBeanNameGenerator.generateBeanName(beanDefinition, context.getDefaultListableBeanFactory());
                        context.getDefaultListableBeanFactory().registerBeanDefinition(beanName,beanDefinition);
                    }
                }

            }

        }

        //用来读取类的信息一个 Spring 操作元数据的工具类
        CachingMetadataReaderFactory cachingMetadataReaderFactory = new CachingMetadataReaderFactory();
        //读取com.ithiema.a05.Config.class的信息
        MetadataReader metadataReader = cachingMetadataReaderFactory.getMetadataReader(new ClassPathResource("com/ithiema/a05/Config.class"));
        //读取到@Bean注解修饰的方法
        Set<MethodMetadata> annotatedMethods =
                metadataReader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
        for(MethodMetadata method :annotatedMethods){
            //获取@Bean注解的属性
            Map<String, Object> annotationAttributes = method.getAnnotationAttributes(Bean.class.getName());
            //针对每一个方法创建bean定义信息对象
            //创建一个定义信息的Builder
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition();
            //我们要把@config标识的类里面的@Bean方法变成工厂方法
            //第一个参数 告知Builder工厂方法是哪一个 就是遍历到的method
            //因为工厂就是@Config标识的类 第二个参数 调用工厂的方法就是该类里面的@Bean修饰的方法 指定工厂名
            beanDefinitionBuilder.setFactoryMethodOnBean(method.getMethodName(),"config");
            //配置自动装配 因为@Bean修饰的工厂方法里面的形参是一个类 需要自动填充并且是根据构造器装配 （规定）
            beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            //@Bean修饰的方法名就是要生成的bean对象的名字 还要传入bean定义信息给bean工厂
            context.getDefaultListableBeanFactory().registerBeanDefinition(method.getMethodName(),beanDefinition);
        }
        // ⬇️初始化容器
        context.refresh();// 执行beanFactory后处理器, 添加bean后处理器, 初始化所有单例
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        Mapper1 mapper1 = context.getBean(Mapper1.class);
        Mapper2 mapper2 = context.getBean(Mapper2.class);
        // ⬇️销毁容器
        context.close();

        /*
            学到了什么
                a. @ComponentScan, @Bean, @Mapper 等注解的解析属于核心容器(即 BeanFactory)的扩展功能
                b. 这些扩展功能由不同的 BeanFactory 后处理器来完成, 其实主要就是补充了一些 bean 定义
         */
    }
}
