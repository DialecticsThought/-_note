package com.itheima.a33;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class WebConfig_1 {
    @Bean // ⬅️内嵌 web 容器工厂
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory(8080);
    }

    @Bean // ⬅️创建 DispatcherServlet
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean // ⬅️注册 DispatcherServlet, Spring MVC 的入口
    public DispatcherServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
    }
    /*
     * 不找@RequestMapping注解 来映射
     * 请求是 /c1  --> 找名字为 /c1的bean 作为控制器
     * 请求是 /c2  -->  找名字为 /c2的bean  作为控制器
     * */
    @Component
    static class MyHandlerMapping implements HandlerMapping {
        /*
        * 有请求的时候 会调用该方法
        * */
        @Override
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            /*
            * 得到请求 /c1 /c2
            * 根据key去找对应的控制器
            * */
            String key = request.getRequestURI();
            Controller controller = collect.get(key);
            if (controller == null) {
                return null;
            }
            //把控制器包装秤执行链（因为涉及到拦截器）
            return new HandlerExecutionChain(controller);
        }

        @Autowired
        private ApplicationContext context;
        private Map<String, Controller> collect;
        /*
        * 定一个初始化的方法
        * 让bean初始化的时候 去找满足条件的bean作为控制器
        * */
        @PostConstruct
        public void init() {
            /*
            * 根据类型（Controller类型）找到bean
            * 得到的是map
            * 过滤掉名字（没有“/”的）bean
            * 过滤之后 放入容器中
            * */
            collect = context.getBeansOfType(Controller.class).entrySet()
                    .stream().filter(e -> e.getKey().startsWith("/"))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            System.out.println(collect);
        }
    }

    @Component
    static class MyHandlerAdapter implements HandlerAdapter {
        /*
        * 判断adapter能否调用控制器
        * */
        @Override
        public boolean supports(Object handler) {
            return handler instanceof Controller;
        }
        /*
        * 处理请求
        * */
        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (handler instanceof Controller controller) {
                controller.handleRequest(request, response);
            }
            //返回null表示不走视图的流程
            return null;
        }

        @Override
        public long getLastModified(HttpServletRequest request, Object handler) {
            return -1;
        }
    }

    @Component("/c1")
    public static class Controller1 implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("this is c1");
            return null;
        }
    }

    @Component("c2")
    public static class Controller2 implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("this is c2");
            return null;
        }
    }

    @Bean("/c3")
    public Controller controller3() {
        return (request, response) -> {
            response.getWriter().print("this is c3");
            return null;
        };
    }
}
