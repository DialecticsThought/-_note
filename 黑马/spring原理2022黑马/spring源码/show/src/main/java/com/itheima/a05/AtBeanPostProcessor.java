package com.itheima.a05;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.Set;

public class AtBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            //用来读取类的信息一个 Spring 操作元数据的工具类
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
            //读取com.ithiema.a05.Config.class的信息
            MetadataReader reader = factory.getMetadataReader(new ClassPathResource("com/itheima/a05/Config.class"));
            //读取到@Bean注解修饰的方法
            Set<MethodMetadata> methods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
            for (MethodMetadata method : methods) {
                System.out.println(method);
                String initMethod = method.getAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
                //针对每一个方法创建bean定义信息对象
                //创建一个定义信息的Builder
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
                //我们要把@config标识的类里面的@Bean方法变成工厂方法
                //第一个参数 告知Builder工厂方法是哪一个 就是遍历到的method
                //因为工厂就是@Config标识的类 第二个参数 调用工厂的方法就是该类里面的@Bean修饰的方法 指定工厂名
                builder.setFactoryMethodOnBean(method.getMethodName(), "config");
                //配置自动装配 因为@Bean修饰的工厂方法里面的形参是一个类 需要自动填充并且是根据构造器装配 （规定）
                builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
                if (initMethod.length() > 0) {
                    builder.setInitMethodName(initMethod);
                }
                AbstractBeanDefinition bd = builder.getBeanDefinition();
                //@Bean修饰的方法名就是要生成的bean对象的名字 还要传入bean定义信息给bean工厂
                beanFactory.registerBeanDefinition(method.getMethodName(), bd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
