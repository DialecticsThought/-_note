package com.itheima.a39;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Configuration
public class A39_1 {

    public static void main(String[] args) throws Exception {
        System.out.println("1. 演示获取 Bean Definition 源");
        SpringApplication spring = new SpringApplication(A39_1.class);
        //添加bean Definition 源
        spring.setSources(Set.of("classpath:b01.xml"));
        System.out.println("2. 演示推断应用类型");
        /*
        * 演示的是web类型
        * deduceFromClasspath
        * 判断
        * 如果类路径下面存在reactive.DispatcherHandler 并且不存在DispatcherServlet
        * 把应用程序类型定位REACTIVE
        * 如果不存在Servlet 和 ConfigurableWebApplicationContext
        * 就认为应用程序类型是非web应用程序
        * 因为该项目引入的是web-starter的依赖 所以是 web应用程序
        * */
        Method deduceFromClasspath = WebApplicationType.class.getDeclaredMethod("deduceFromClasspath");
        deduceFromClasspath.setAccessible(true);
        System.out.println("\t应用类型为:"+deduceFromClasspath.invoke(null));
        System.out.println("3. 演示 ApplicationContext 初始化器  对容器扩展功能");
        spring.addInitializers(applicationContext -> {
            if (applicationContext instanceof GenericApplicationContext gac) {
                /*
                * bean1是xml配置提供
                * bean3是配置类提供
                * bean3有初始化器提供
                * */
                gac.registerBean("bean3", Bean3.class);
            }
        });
        System.out.println("4. 演示监听器与事件 ");
        spring.addListeners(event -> System.out.println("\t事件为:" + event.getClass()));
        System.out.println("5. 演示主类推断 记录运行main方法的所在类是哪个");
        Method deduceMainApplicationClass = SpringApplication.class.getDeclaredMethod("deduceMainApplicationClass");
        deduceMainApplicationClass.setAccessible(true);
        //打印出main类的全路径
        System.out.println("\t主类是："+deduceMainApplicationClass.invoke(spring));
        /*
        * run方法才会创建和初始化springboot容器
        * */
        ConfigurableApplicationContext context = spring.run(args);

        // 创建 ApplicationContext
        // 调用初始化器 对 ApplicationContext 做扩展
        // ApplicationContext.refresh
        for (String name : context.getBeanDefinitionNames()) {

            System.out.println("name: " + name + " 来源：" + context.getBeanFactory().getBeanDefinition(name).getResourceDescription());
        }
        context.close();
        /*
            学到了什么
            a. SpringApplication 构造方法中所做的操作
                1. 可以有多种源用来加载 bean 定义
                2. 应用类型推断
                3. 容器初始化器
                4. 演示启动各阶段事件
                5. 演示主类推断
         */
    }

    static class Bean1 {

    }

    static class Bean2 {

    }

    static class Bean3 {

    }

    @Bean
    public Bean2 bean2() {
        return new Bean2();
    }

    @Bean
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
