package com.itheima.a32;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Configuration
public class WebConfig {

    @Bean
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }
    /*
    * 把DispatcherServlet注册到Tomcat中
    * */
    @Bean
    public DispatcherServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        DispatcherServletRegistrationBean registrationBean = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

    @Bean // @RequestMapping  提供路径映射器  没有的话 会有默认映射器
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }
    /*
    *  RequestMappingHandlerAdapter
    * 执行控制器方法
    * 调用之前解析参数
    * 调用之后处理返回值
    * */
    @Bean // 注意默认的 RequestMappingHandlerAdapter 不会带 jackson 转换器
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
        handlerAdapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
        return handlerAdapter;
    }

    @Bean // 修改了 Tomcat 服务器默认错误地址
    public ErrorPageRegistrar errorPageRegistrar() { // 出现错误，会使用请求转发 forward 跳转到 error 地址
        //该方法的形参就是TomcatServletWebServerFactory
        return webServerFactory -> webServerFactory.addErrorPages(new ErrorPage("/error"));
    }
    /*
    * 这个后置处理器就是在TomcatServletWebServerFactory初始化之前执行
    * 到容器中找到所有ErrorPageRegistrar
    * 给webServerFactory添加ErrorPages
    * */
    @Bean
    public ErrorPageRegistrarBeanPostProcessor errorPageRegistrarBeanPostProcessor() {
        return new ErrorPageRegistrarBeanPostProcessor();
    }

    @Controller
    public static class MyController {
        /*
        * 这个方法出异常 就会把异常放入request作用域
        * */
        @RequestMapping("test")
        public ModelAndView test() {
            int i = 1 / 0;
            return null;
        }

        /*
        * 从request作用域取出异常
        * 把异常以json的方法些人请求体返回给前端
        * */
        /*@RequestMapping("/error")
        @ResponseBody
        public Map<String, Object> error(HttpServletRequest request) {
            Throwable e = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            return Map.of("error", e.getMessage());
        }*/
    }

    @Bean// ⬅️ErrorProperties 封装环境键值, ErrorAttributes 控制有哪些错误信息
    public BasicErrorController basicErrorController() {
        //把异常信息包含在响应结果里面
        // errorProperties 代替配置文件中的server.error.include-exception=true
        ErrorProperties errorProperties = new ErrorProperties();
        errorProperties.setIncludeException(true);
        return new BasicErrorController(new DefaultErrorAttributes(), errorProperties);
    }
    /*
    * 如果浏览器发的请求 BasicErrorController返回的是名称为 error的ModelAndView视图
    * 所以要创建一个视图
    * */
    @Bean// ⬅️名称为 error 的视图, 作为 BasicErrorController 的 text/html 响应结果
    public View error() {
        return new View() {
            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                System.out.println(model);
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().print("""
                        <h3>服务器内部错误</h3>
                        """);
            }
        };
    }

    @Bean // ⬅️收集容器中所有 View 对象, bean 的名字作为视图名
    public ViewResolver viewResolver() {
        /*
        * 只要类型是view类型
        * 按照bean的名字找到视图  也就是说没了BeanNameViewResolver解析器 就找不到error视图
        * 这个解析器会丢给前端控制器调用
        * */
        return new BeanNameViewResolver();
    }
}
