package com.itheima.a41;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

public class TestDataSourceAuto {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource(
                "--spring.datasource.url=jdbc:mysql://localhost:3306/test",
                "--spring.datasource.username=root",
                "--spring.datasource.password=123456"
        ));
        context.setEnvironment(env);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);

        String packageName = TestDataSourceAuto.class.getPackageName();
        System.out.println("当前包名:" + packageName);
        AutoConfigurationPackages.register(context.getDefaultListableBeanFactory(),
                packageName);

        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            //得到bean的来源 是哪一个配置类或者是配置文件提供的
            String resourceDescription = context.getBeanDefinition(name).getResourceDescription();
            if (resourceDescription != null)
                System.out.println(name + " 来源:" + resourceDescription);
        }
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    DataSourceAutoConfiguration.class.getName(),//数据源的自动配置类
                    /*
                    * @ConditionalOnSingleCandidate(DataSource.class)
                    *       表示容器只仅有一个DataSource成立 因为SQLSession创建只能针对一个DataSource
                    * @EnableConfigurationProperties({MybatisProperties.class})
                    *       读取MybatisProperties对象 该对象与配置文件的配置绑定
                    * @AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class})
                    *       多个配置类的解析顺序  因为mybatis的配置类必须在DataSource配置类后面解析 因为mybatis的解析需要DataSource
                    *
                    * public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) 方法 ☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
                    *       不用Mybatis提供DefaultSqlSession实现 用sqlSessionTemplate的原因与当前线程绑定的SqlSession 保证多个方法调用只要是同一个线程 话就是相同的SqlSession
                    * MapperFactoryBean类
                    *       用来生产一个mapper对象 通过getObject方法 getObject方法里面调用的是getSqlSession方法
                    *       getSqlSession方法实际调用的就是sqlSessionTemplate方法
                    *
                    * @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})
                    * @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
                    * MapperScannerRegistrarNotFoundConfiguration类
                    *       容器中必须缺失MapperFactoryBean和 MapperScannerConfigure（beanFactory后置处理器 用来mapper扫描）
                    *        成立该条件导入MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar
                    *       AutoConfiguredMapperScannerRegistrar实现了接口ImportBeanDefinitionRegistrar 允许用编程的方式补充beanDefinition
                    *       根据mapper接口类型把每个mapper接口封装成mapperFactoryBean 作为beanDefinition放入工厂
                    *        AutoConfiguredMapperScannerRegistrar本质上实现了mapper扫描 但是要配合包名 扫描包名下的@mapper修饰的mapper接口☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
                    *        包名让@AutoConfigurationPackage提供
                    *
                    *
                    * @SpringBootConfiguration 代表了这是一个配置类
                    * @EnableAutoConfiguration  导入自动配置类
                    * @EnableAutoConfiguration这个注解中有一个@AutoConfigurationPackage  这个注解可以记录引导类的所在包  用来mapper扫描
                    *
                    * */
                    MybatisAutoConfiguration.class.getName(),//
                    /*
                    *  DataSourceTransactionManagerAutoConfiguration 里面
                    *  有一个方法DataSourceTransactionManager transactionManager(....)
                    *  这个方法@ConditionalOnMissingBean(TransactionManager.class)修饰表示如果容器中没有TransactionManager就要执行
                    *  执行结果生成transactionManager对象
                    *
                    * */
                    DataSourceTransactionManagerAutoConfiguration.class.getName(),//提交回滚事务
                    /*
                    *
                    * 底层有ProxyTransactionManagementConfiguration类  这个类有一个transactionAdvisor这个核心方法
                    * 该方法有2个形参：一个形参TransactionAttributeSource代表的切点 另一个参数 TransactionInterceptor代表着通知增强
                    *
                    * */
                    TransactionAutoConfiguration.class.getName()//提供声明式事务管理事务切面
            };
        }
    }
}
