package org.springframework.boot;

import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

public class Step4 {

    public static void main(String[] args) throws IOException, NoSuchFieldException {
        /*
         * 配置信息的抽象
         * 系统配置可以来自系统环境变量, properties, yaml等
         * 把这些信息总和出来
         * 环境对象里面只有2个来源: 系统属性(java的) 系统变量（电脑os的）
         * */
        ApplicationEnvironment env = new ApplicationEnvironment();
        //添加来源addLast添加到最后 优先级最低
        env.getPropertySources().addLast(
                new ResourcePropertySource("step4", new ClassPathResource("step4.properties"))
        );
        //添加来源addFirst添加到最后 优先级最高
        //这个来源是可以通过 属性名中有“-” 找到properties对应的属性值
        ConfigurationPropertySources.attach(env);
        //遍历环境对象的来源 就2个
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }

        System.out.println(env.getProperty("user.first-name"));
        System.out.println(env.getProperty("user.middle-name"));
        System.out.println(env.getProperty("user.last-name"));
    }
}
