package com.itheima.a41;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

public class TestAopAuto {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        StandardEnvironment env = new StandardEnvironment();
        //往环境中添加配置 spring.aop.auto=true 让AopAutoConfiguration加载
//        env.getPropertySources().addLast(new SimpleCommandLinePropertySource("--spring.aop.auto=true"));
        context.setEnvironment(env);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(">>>>>>>>>>>>>>>");
        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(
                "org.springframework.aop.config.internalAutoProxyCreator", AnnotationAwareAspectJAutoProxyCreator.class);
        System.out.println(creator.isProxyTargetClass());

    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }
    /*
    * @ConditionalOnProperty(prefix = "spring.aop",name = {"auto"},havingValue = "true",matchIfMissing = true)
    * 在配置文件中找spring.aop.auto = true 如果有 就加载AopAutoConfiguration.class
    * 但是matchIfMissing = true表示 即使没有该配置项 也要加载
    *
    * 检查类路径是否存在Advice.class 因为Springboot默认配置Advice所以进入该if判断条件
    *   判断配置文件中找spring.aop.proxy-target-class 是否存在
    *       存在 加载JdkDynamicAutoProxyConfiguration
    *               该类上@EnableAspectJAutoProxy表示注入AutoProxyCreator proxyTargetClass = false 创建代理的话 如果目标实现接口优先AOP
    *       不存在 加载CglibAutoProxyConfiguration
    *               该类上@EnableAspectJAutoProxy表示注入AutoProxyCreator proxyTargetClass = true 创建代理的话  优先cglib
    * 检查类路径是否不存在Advice.class
    * */
    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AopAutoConfiguration.class.getName()};
        }
    }
}
