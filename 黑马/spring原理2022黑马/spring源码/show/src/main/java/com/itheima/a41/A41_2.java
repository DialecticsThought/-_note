package com.itheima.a41;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.util.List;

public class A41_2 {

    @SuppressWarnings("all")
    public static void main(String[] args) throws IOException {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext();
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource(
                "--spring.datasource.url=jdbc:mysql://localhost:3306/test",
                "--spring.datasource.username=root",
                "--spring.datasource.password=root"
        ));
        context.setEnvironment(env);
        context.registerBean("config", Config.class);
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            String resourceDescription = context.getBeanDefinition(name).getResourceDescription();
            if (resourceDescription != null)
                System.out.println(name + " 来源:" + resourceDescription);
        }
        context.close();
    }

    @Configuration // 本项目的配置类
//    @Import(AutoConfigurationImportSelector.class)
    @EnableAutoConfiguration
    static class Config {
        @Bean
        public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }
    }

    static class MyImportSelector implements DeferredImportSelector {
        // ⬇️该方法从 META-INF/spring.factories 读取自动配置类名，返回的 String[] 即为要导入的配置类
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            /*
            * 从spring.Factories读取配置 key是MyImportSelector value读出来引入对应的对象到容器中
            * 同理：@EnableAutoConfiguration注解都是样子货 本质上看@Import注解 也就是@Import(AutoConfigurationImportSelector.class)
            * AutoConfigurationImportSelector也会读取spring.Factories的配置 引入相应的对象
            * */
            return SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, null).toArray(new String[0]);
        }
    }

    @Configuration // 第三方的配置类
    static class AutoConfiguration1 {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }
    }

    @Configuration // 第三方的配置类
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {

    }
    static class Bean2 {

    }
}
