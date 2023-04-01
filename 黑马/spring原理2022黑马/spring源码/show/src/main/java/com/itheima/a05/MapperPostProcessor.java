package com.itheima.a05;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;

public class MapperPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            //得到通配符的解析器
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            //扫描具体某一个包
            Resource[] resources = resolver.getResources("classpath:com/itheima/a05/mapper/**/*.class");
            //因为把bean的定义信息注册到bean工厂 需要bean名字 所以创建一个bean名字生成器 这个生成器是根据BeanDefinition生成名字
            AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
            //生成一个能读取元信息的工厂
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
            for (Resource resource : resources) {//遍历扫描到的资源
                //读取信息
                MetadataReader reader = factory.getMetadataReader(resource);
                ClassMetadata classMetadata = reader.getClassMetadata();//得到原信息
                if (classMetadata.isInterface()) {//判断读取到的资源是不是接口类型（mapper接口）
                    //读取定义的信息
                    AbstractBeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class)
                            /*
                            * MapperFactoryBean<Mapper1> factory = new MapperFactoryBean<>(Mapper1.class);
                            * 用到了构造方法 这个构造方法的参数就是Mapper1.class 也就是classMetadata.getClassName()
                            * */
                            .addConstructorArgValue(classMetadata.getClassName())
                            /*
                            *  factory.setSqlSessionFactory(sqlSessionFactory);
                            *  sqlSessionFactory可以根据类型从容器中获取
                            * */
                            .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                            .getBeanDefinition();
                            //这个bd2不放入容器仅仅是为了生成名字而创建的 防止 AnnotationBeanNameGenerator重复使用bd生成多个相同名字 那么根据相同名字生成的bean对象会覆盖掉前面的bean对象
                    AbstractBeanDefinition bd2 = BeanDefinitionBuilder.genericBeanDefinition(classMetadata.getClassName()).getBeanDefinition();
                    //生成bean的名字
                    String name = generator.generateBeanName(bd2, beanFactory);
                    //把bean的定义信息注册到bean工厂
                    beanFactory.registerBeanDefinition(name, bd);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
