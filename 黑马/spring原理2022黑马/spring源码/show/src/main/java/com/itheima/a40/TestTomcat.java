package com.itheima.a40;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestTomcat {
    /*
    * tomcat 不能直接识别servlet, controller等
    * tomcat 在3.0版本之后不通过web.xml直接添加servlet, filter, listener
        Server
        └───Service
            ├───Connector组件 (请求以什么协议, 什么端口连接到服务器)
            └───Engine
                └───Host(虚拟主机 localhost)
                    ├───Context1 (应用1, 可以设置 ①虚拟路径, / 即 url 起始路径; ②项目磁盘路径, 即 docBase )
                    │   │   index.html
                    │   └───WEB-INF
                    │       │   web.xml (servlet, filter, listener) 3.0
                    │       ├───classes (servlet, controller, service ...)
                    │       ├───jsp
                    │       └───lib (第三方 jar 包)
                    └───Context2 (应用2)
                        │   index.html
                        └───WEB-INF
                                web.xml
     *
     * 1~4步骤是容器执行onRefresh方法执行的
     * 5~6步骤是容器执行finishRefresh方法执行的
     */
    @SuppressWarnings("all")
    public static void main(String[] args) throws LifecycleException, IOException {
        // 1.创建 Tomcat 服务器 对象
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("tomcat");//设置基础目录  存放临时文件

        // 2.创建项目文件夹, 即 docBase 文件夹 给Context使用
        File docBase = Files.createTempDirectory("boot.").toFile();
        docBase.deleteOnExit();//程序退出自动删除

        // 3.创建 Tomcat 项目, 在 Tomcat 中称为 Context
        //指定"/"为虚拟目录
        Context context = tomcat.addContext("", docBase.getAbsolutePath());

        //得到spring容器
        WebApplicationContext springContext = getApplicationContext();

        // 4.编程添加 Servlet（这个Servlet定在另一个类）
        context.addServletContainerInitializer(new ServletContainerInitializer() {
            /*
            * 这个方法会传入一个ctx
            * */
            @Override
            public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
                //创建一个Servlet
                HelloServlet helloServlet = new HelloServlet();
                //添加到ctx
                ctx.addServlet("aaa", helloServlet).addMapping("/hello");
                /*
                * 在Spring容器里面得到dispatcherServlet 再嵌入ctx
                * */
//                DispatcherServlet dispatcherServlet = springContext.getBean(DispatcherServlet.class);
//                ctx.addServlet("dispatcherServlet", dispatcherServlet).addMapping("/");
                /*
                * springContext.getBeansOfType(ServletRegistrationBean.class)得到的是map
                * map的value就是registrationBean
                 * */
                for (ServletRegistrationBean registrationBean : springContext.getBeansOfType(ServletRegistrationBean.class).values()) {
                    /*
                    * 该方法救就是执行ctx.addServlet("dispatcherServlet", dispatcherServlet).addMapping("/")类似的方法
                    * */
                    registrationBean.onStartup(ctx);
                }
            }
        }, Collections.emptySet());

        // 5.启动 Tomcat
        tomcat.start();

        // 6.创建连接器, 设置监听端口
        Connector connector = new Connector(new Http11Nio2Protocol());
        connector.setPort(8080);
        tomcat.setConnector(connector);
    }
    /*
    * 创建Spring容器
    * */
    public static WebApplicationContext getApplicationContext() {
//        AnnotationConfigServletWebServerApplicationContext  已经内嵌了tomcat
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        //手动注册配置类
        context.register(Config.class);
        //手动初始化
        context.refresh();
        return context;
    }

    @Configuration
    static class Config {
        /*
        * 容器里面扫描到所有的ServletRegistrationBean
        * ServletRegistrationBean再去注册
        * */
        @Bean
        public DispatcherServletRegistrationBean registrationBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        @Bean
        // 这个例子中必须为 DispatcherServlet 提供 AnnotationConfigWebApplicationContext, 否则会选择 XmlWebApplicationContext 实现
        public DispatcherServlet dispatcherServlet(WebApplicationContext applicationContext) {
            return new DispatcherServlet(applicationContext);
        }
        /*
        * 自定义是因为默认提供的是Adapter没有JSON转换器
        * */
        @Bean
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
            handlerAdapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            return handlerAdapter;
        }

        @RestController
        static class MyController {
            @GetMapping("hello2")
            public Map<String,Object> hello() {
                return Map.of("hello2", "hello2, spring!");
            }
        }
    }
}
