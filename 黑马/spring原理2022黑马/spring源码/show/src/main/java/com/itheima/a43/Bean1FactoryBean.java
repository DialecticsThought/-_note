package com.itheima.a43;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/*
* 工厂作为一个bean自身被spring管理
* Spring只创建了工厂 工厂调用getObject()创建bean1  bean1不是被Spring直接创建
* bean1的依赖注入（set） 初始化 和Aware方法都没有被Spring调用
* 因为bean1么偶又被spring容器管理
* 针对bean1的后置处理器的初始化前增强没有执行 初始化后增强执行了
 * */
@Component("bean1")
public class Bean1FactoryBean implements FactoryBean<Bean1> {

    private static final Logger log = LoggerFactory.getLogger(Bean1FactoryBean.class);

    // 决定了根据【类型】获取或依赖注入能否成功
    @Override
    public Class<?> getObjectType() {
        return Bean1.class;
    }

    // 决定了 getObject() 方法被调用一次还是多次
    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Bean1 getObject() throws Exception {
        Bean1 bean1 = new Bean1();
        log.debug("create bean: {}", bean1);
        return bean1;
    }
}
