package com.itheima.a03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class LifeCycleBean {
    private static final Logger log = LoggerFactory.getLogger(LifeCycleBean.class);
    //执行循序 1 构造函数
    public LifeCycleBean() {
        log.debug("构造");
    }
    //执行循序 2 依赖注入
    @Autowired
    public void autowire(@Value("${JAVA_HOME}") String home) {
        log.debug("依赖注入: {}", home);
    }
    //执行循序 3 初始化
    @PostConstruct
    public void init() {
        log.debug("初始化");
    }
    //执行循序 4 销毁
    @PreDestroy
    public void destroy() {
        log.debug("销毁");
    }
}
