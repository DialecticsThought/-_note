package com.itheima.a21;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
    目标: 解析控制器方法的参数值

    常见的参数处理器如下:
        org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@abbc908
        org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver@44afefd5
        org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver@9a7a808
        org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver@72209d93
        org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver@2687f956
        org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver@1ded7b14
        org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@29be7749
        org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor@5f84abe8
        org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver@4650a407
        org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver@30135202
        org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver@6a4d7f76
        org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver@10ec523c
        org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver@53dfacba
        org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver@79767781
        org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver@78411116
        org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver@aced190
        org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver@245a060f
        org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor@6edaa77a
        org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver@1e63d216
        org.springframework.web.method.annotation.ModelMethodProcessor@62ddd21b
        org.springframework.web.method.annotation.MapMethodProcessor@16c3ca31
        org.springframework.web.method.annotation.ErrorsMethodArgumentResolver@2d195ee4
        org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver@2d6aca33
        org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver@21ab988f
        org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver@29314cc9
        org.springframework.web.method.annotation.RequestParamMethodArgumentResolver@4e38d975
        org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor@35f8a9d3
 */
public class A21 {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        //因为形参有类似${JAVA_HOME} 解析的功能要beanFactory
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        // 准备测试 Request
        HttpServletRequest request = mockRequest();

        // 要点1. 控制器方法被封装为 HandlerMethod
        HandlerMethod handlerMethod = new HandlerMethod(new Controller(), Controller.class.getMethod("test", String.class, String.class, int.class, String.class, MultipartFile.class, int.class, String.class, String.class, String.class, HttpServletRequest.class, User.class, User.class, User.class));

        // 要点2. 准备对象绑定与类型转换  假设传递了一个 参数是18  形参是int类型 age  那么要把18转成int类型 赋值给age
        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, null);

        // 要点3. 准备 ModelAndViewContainer 用来存储中间 Model 结果 有的解析器解析的过程中会有model数据
        ModelAndViewContainer container = new ModelAndViewContainer();

        // 要点4. 解析每个参数值  获取控制器方法的每个形参
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            // 多个解析器组合  体现了一个设计模式叫做组合器的设计模式
            HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
            //添加参数解析器
            composite.addResolvers(
                    //                                          false 表示必须有 @RequestParam注解
                    new RequestParamMethodArgumentResolver(beanFactory, false),
                    new PathVariableMethodArgumentResolver(),//@PathVariable
                    new RequestHeaderMethodArgumentResolver(beanFactory),
                    new ServletCookieValueMethodArgumentResolver(beanFactory),//解析@CookieValue
                    new ExpressionValueMethodArgumentResolver(beanFactory),//解析#{} ${}
                    new ServletRequestMethodArgumentResolver(),//解析HttpServletRequest
                    new ServletModelAttributeMethodProcessor(false), // 必须有 @ModelAttribute
                    new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),//从请求体
                    new ServletModelAttributeMethodProcessor(true), // true表示可以省略了 @ModelAttribute
                    new RequestParamMethodArgumentResolver(beanFactory, true) // true可以省略 @RequestParam
            );
            //得到注解
            String annotations = Arrays.stream(parameter.getParameterAnnotations()).map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining());
            String str = annotations.length() > 0 ? " @" + annotations + " " : " ";
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            //多个解析器一个个帕努单是否支持此参数
            if (composite.supportsParameter(parameter)) {//是否支持这个参数 支持就解析
                // 解析器解析参数
                Object v = composite.resolveArgument(parameter, container, new ServletWebRequest(request), factory);
//                System.out.println(v.getClass());
                System.out.println("[" + parameter.getParameterIndex() + "] " + str + parameter.getParameterType().getSimpleName() + " " + parameter.getParameterName() + "->" + v);
                System.out.println("模型数据为：" + container.getModel());
            } else {
                System.out.println("[" + parameter.getParameterIndex() + "] " + str + parameter.getParameterType().getSimpleName() + " " + parameter.getParameterName());
            }
        }

        /*
            学到了什么
                a. 每个参数处理器能干啥
                    1) 看是否支持某种参数
                    2) 获取参数的值
                b. 组合模式在 Spring 中的体现
                c. @RequestParam, @CookieValue 等注解中的参数名、默认值, 都可以写成活的, 即从 ${ } #{ }中获取
         */
    }
    //模拟一个请求
    private static HttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name1", "zhangsan");
        request.setParameter("name2", "lisi");
        request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
        // 会把当前实际的url路径和@RequestMapping的url路径做匹配  会得到<id,123>
        //@PathVariable("id") 去map找 value
        Map<String, String> map = new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        System.out.println(map);
        //把映射放到作用域里面
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
        //设置请求头
        request.setContentType("application/json");
        request.setCookies(new Cookie("token", "123456"));
        request.setParameter("name", "张三");
        request.setParameter("age", "18");
/*        request.setContent("""
                    {
                        "name":"李四",
                        "age":20
                    }
                """.getBytes(StandardCharsets.UTF_8));*/

        return new StandardServletMultipartResolver().resolveMultipart(request);
    }

    static class Controller {
        public void test(
                /*
                * 下面的参数都有对应的参数解析器
                * */
                @RequestParam("name1") String name1, // name1=张三
                String name2,                        // name2=李四  方法的参数名和请求名相同的话自动匹配
                @RequestParam("age") int age,        // age=18  从字符串转成int类型
                @RequestParam(name = "home", defaultValue = "${JAVA_HOME}") String home1, // spring 获取数据
                @RequestParam("file") MultipartFile file, // 上传文件
                @PathVariable("id") int id,               //  /test/124   /test/{id}
                @RequestHeader("Content-Type") String header,
                @CookieValue("token") String token,
                @Value("${JAVA_HOME}") String home2, // spring 获取数据  ${} #{}
                HttpServletRequest request,          // request, response, session ...
                @ModelAttribute("abc") User user1,          // name=zhang&age=18
                User user2,                          // name=zhang&age=18 只不过省略了@ModelAttribute
                @RequestBody User user3              // json 从请求体里面获取数据
        ) {
        }
    }

    static class User {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                   "name='" + name + '\'' +
                   ", age=" + age +
                   '}';
        }
    }
}
