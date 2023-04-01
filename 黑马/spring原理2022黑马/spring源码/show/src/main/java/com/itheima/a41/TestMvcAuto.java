package com.itheima.a41;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;

public class TestMvcAuto {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext();
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            String source = context.getBeanDefinition(name).getResourceDescription();
            if (source != null) {
                System.out.println(name + " 来源:" + source);
            }
        }
        context.close();

    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {
    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    /*
                    * 内嵌tomcat等等服务器工厂
                    * 提供 ServletWebServerFactory
                    * */
                    ServletWebServerFactoryAutoConfiguration.class.getName(),
                    /*
                    * 自动配置DispatcherServlet
                    * */
                    DispatcherServletAutoConfiguration.class.getName(),
                    /*
                    * 自动配置DispatcherServlet所需的各种组件
                    * * 多项 HandlerMapping
                    * 多项 HandlerAdapter
                    * HandlerExceptionResolver
                    * */
                    WebMvcAutoConfiguration.class.getName(),
                    /*
                    * 自动配置error处理器
                    * */
                    ErrorMvcAutoConfiguration.class.getName()
            };
        }
    }
}
