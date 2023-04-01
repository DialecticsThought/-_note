package com.itheima.a41;

import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class A41_1 {

    @SuppressWarnings("all")
    public static void main(String[] args) throws IOException {
        GenericApplicationContext context = new GenericApplicationContext();
        /*
        *  beanFactory不可以让后面注册的bean覆盖前面注册的bean
        * */
        context.getDefaultListableBeanFactory().setAllowBeanDefinitionOverriding(false);
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();
        /*
        * 遍历容器中bean的名字
        * */
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(context.getBean(Bean1.class));
    }
    /*
    * 通过@Import在本项目的配置类导入其他的配置类
    * @Import引入到的对象 和本配置类引入的对象 名称相同的时候
    * 最终留下的本配置类引入的对象
    * @Import引入到的对象先解析 再解析本配置引入的对象（ImportSelector是继承了ImportSelector）
    * beanFactory可以让后面注册的bean覆盖前面注册的bean（默认不允许覆盖 需要改配置）
    * 如果引入的ImportSelector是继承了DeferredImportSelector
    * 就是推迟导入  解析本配置引入的对象 再解析@Import引入到的对象
    * */
    @Configuration // 本项目的配置类
    @Import(MyImportSelector.class)
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1("本项目");
        }
    }
    /*
    * 配合@Import使用的选择器
    * 实现方法的返回值就是要导入的类名所形成的数组
    * */
    static class MyImportSelector implements DeferredImportSelector {
        // ⬇️该方法从 META-INF/spring.factories 读取自动配置类名，返回的 String[] 即为要导入的配置类
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//            for (String name : SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, null)) {
//                System.out.println(name);
//            }
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            /*
            * SpringFactoriesLoader.loadFactoryNames是读取resources目录下META-INF目录下的spring.factories
            * 不仅仅是这样 只要本项目关联的所有jar包下的spring.factories文件都会读取
            * MyImportSelector.class表示 key是MyImportSelector.class内部类
            * （因为spring.factories文件中有个key就叫ImportSelector.class）
            * 读取之后转成字符串的数组 依照数组里面的名字Import
            *
            * 实现了解耦 因为 可以改配置文件来实现引入不同的类☆☆☆☆☆☆☆
            * */
            List<String> names = SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, null);
            return names.toArray(new String[0]);
        }
    }

    @Configuration // ⬅️第三方的配置类
    static class AutoConfiguration1 {
        /*
        * 让本地配置类先加载
        * 又设置 beanFactory不可以让后面注册的bean覆盖前面注册的bean
        * 再加上@ConditionalOnMissingBean
        * 就可以让第三方配置类的bean1在本地配置类不存在bean1的时候创建
        * */
        @Bean
        @ConditionalOnMissingBean//当没有bean1的时候 调用方法
        public Bean1 bean1() {
            return new Bean1("第三方");
        }
    }

    @Configuration // ⬅️第三方的配置类
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean2 {

    }

    static class Bean1 {
        private String name;

        public Bean1() {
        }

        public Bean1(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Bean1{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
