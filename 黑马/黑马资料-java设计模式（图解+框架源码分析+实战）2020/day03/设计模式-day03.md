# 5，结构型模式

结构型模式描述如何将类或对象按某种布局组成更大的结构。它分为类结构型模式和对象结构型模式，前者采用继承机制来组织接口和类，后者釆用组合或聚合来组合对象。

由于组合关系或聚合关系比继承关系耦合度低，满足“合成复用原则”，所以对象结构型模式比类结构型模式具有更大的灵活性。

结构型模式分为以下 7 种：

* 代理模式
* 适配器模式
* 装饰者模式
* 桥接模式
* 外观模式
* 组合模式
* 享元模式



## 5.1 代理模式

### 5.1.1 概述

由于某些原因需要给某对象提供一个代理以控制对该对象的访问。这时，访问对象不适合或者不能直接引用目标对象，**代理对象作为访问对象和目标对象之间的中介**。

Java中的代理按照代理类生成时机不同又分为静态代理和动态代理。**静态代理代理类在编译期就生成，而动态代理代理类则是在Java运行时动态生成。动态代理又有JDK代理和CGLib代理两种**。

### 5.1.2 结构

代理（Proxy）模式分为三种角色：

* 抽象主题（Subject）类： 通过接口或抽象类声明真实主题和代理对象实现的业务方法。
* 真实主题（Real Subject）类： 实现了抽象主题中的具体业务，是代理对象所代表的真实对象，是最终要引用的对象。
* 代理（Proxy）类 ： 提供了与真实主题相同的接口，其内部含有对真实主题的引用，它可以访问、控制或扩展真实主题的功能。

### 5.1.3 静态代理

我们通过案例来感受一下静态代理。

【例】火车站卖票

如果要买火车票的话，需要去火车站买票，坐车到火车站，排队等一系列的操作，显然比较麻烦。而火车站在多个地方都有代售点，我们去代售点买票就方便很多了。这个例子其实就是典型的代理模式，火车站是目标对象，代售点是代理对象。类图如下：

<img src="img\静态代理.png" style="zoom:80%;" />

代码如下：

SellTickets

```java
/**
 * @version v1.0
 * @ClassName: SellTickets
 * @Description: 卖火车票的接口
 * @Author: 黑马程序员
 */
public interface SellTickets {

    void sell();
}
```

TrainStation 

```java
/**
 * @version v1.0
 * @ClassName: TrainStation
 * @Description: 火车站类
 * @Author: 黑马程序员
 */
public class TrainStation implements SellTickets {

    public void sell() {
        System.out.println("火车站卖票");
    }
}
```

ProxyPoint

```java
/**
 * @version v1.0
 * @ClassName: ProxyPoint
 * @Description: 代售点类
 * @Author: 黑马程序员
 */
public class ProxyPoint implements SellTickets {

    //声明火车站类对象
    private TrainStation trainStation  = new TrainStation();

    public void sell() {
        //这行代码相当于增强
        System.out.println("代售点收取一些服务费用");
        trainStation.sell();
    }

}
```

 Client

```java
/**
 * @version v1.0
 * @ClassName: Client
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class Client {
    public static void main(String[] args) {
        //创建代售点类对象
        ProxyPoint proxyPoint = new ProxyPoint();
        //调用方法进行买票
        proxyPoint.sell();
    }
}
```

从上面代码中可以看出测试类直接访问的是ProxyPoint类对象，也就是说ProxyPoint作为访问对象和目标对象的中介。同时也对sell方法进行了增强（代理点收取一些服务费用）。

### 5.1.4 JDK动态代理

接下来我们使用动态代理实现上面案例，先说说JDK提供的动态代理。Java中提供了一个动态代理类Proxy，Proxy并不是我们上述所说的代理对象的类，**而是提供了一个创建代理对象的静态方法（newProxyInstance方法）来获取代理对象。在内存中动态的生成代理类**

代码如下：

```java
/**
 * @version v1.0
 * @ClassName: SellTickets
 * @Description: 卖火车票的接口
 * @Author: 黑马程序员
 */
public interface SellTickets {

    void sell();
}
```

TrainStation

```java
/**
 * @version v1.0
 * @ClassName: TrainStation
 * @Description: 火车站类
 * @Author: 黑马程序员
 */
public class TrainStation implements SellTickets {

    public void sell() {
        System.out.println("火车站卖票");
    }
}
```

ProxyFactory

```java
/**
 * @version v1.0
 * @ClassName: ProxyFactory
 * @Description: 获取代理对象的工厂类
 *      代理类也实现了对应的接口
 * @Author: 黑马程序员
 */
public class ProxyFactory {

    //声明目标对象
    private TrainStation station = new TrainStation();

    //获取代理对象的方法
    public SellTickets getProxyObject() {
        //返回代理对象
        /*
            ClassLoader loader : 类加载器，用于加载代理类。可以通过目标对象获取类加载器
            Class<?>[] interfaces ： 代理类实现的接口的字节码对象
            InvocationHandler h ： 代理对象的调用处理程序
         */
        SellTickets proxyObject = (SellTickets)Proxy.newProxyInstance(
                station.getClass().getClassLoader(),
                station.getClass().getInterfaces(),
                new InvocationHandler() {
                    /*
                        Object proxy : 代理对象。和proxyObject对象是同一个对象，在invoke方法中基本不用
                        Method method ： 对接口中的方法进行封装的method对象
                        Object[] args ： 调用方法的实际参数

                        返回值： 方法的返回值。
                     */
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //System.out.println("invoke方法执行了");
                        System.out.println("代售点收取一定的服务费用(jdk动态代理)");
                        //执行目标对象的方法
                        Object obj = method.invoke(station, args);
                        return obj;
                    }
                }
        );
        return proxyObject;
    }
}
```

<font color="red">使用了动态代理，我们思考下面问题：</font>

* ProxyFactory是代理类吗？

  ProxyFactory不是代理模式中所说的代理类，**而代理类是程序在运行过程中动态的在内存中生成的类**。通过阿里巴巴开源的 Java 诊断工具（Arthas【阿尔萨斯】）查看代理类的结构：

  ```java
  package com.sun.proxy;
  
  import com.itheima.proxy.dynamic.jdk.SellTickets;
  import java.lang.reflect.InvocationHandler;
  import java.lang.reflect.Method;
  import java.lang.reflect.Proxy;
  import java.lang.reflect.UndeclaredThrowableException;
  
  public final class $Proxy0 extends Proxy implements SellTickets {
      private static Method m1;
      private static Method m2;
      private static Method m3;
      private static Method m0;
  
      public $Proxy0(InvocationHandler invocationHandler) {
          super(invocationHandler);
      }
  
      static {
          try {
              m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
              m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
              m3 = Class.forName("com.itheima.proxy.dynamic.jdk.SellTickets").getMethod("sell", new Class[0]);
              m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
              return;
          }
          catch (NoSuchMethodException noSuchMethodException) {
              throw new NoSuchMethodError(noSuchMethodException.getMessage());
          }
          catch (ClassNotFoundException classNotFoundException) {
              throw new NoClassDefFoundError(classNotFoundException.getMessage());
          }
      }
  
      public final boolean equals(Object object) {
          try {
              return (Boolean)this.h.invoke(this, m1, new Object[]{object});
          }
          catch (Error | RuntimeException throwable) {
              throw throwable;
          }
          catch (Throwable throwable) {
              throw new UndeclaredThrowableException(throwable);
          }
      }
  
      public final String toString() {
          try {
              return (String)this.h.invoke(this, m2, null);
          }
          catch (Error | RuntimeException throwable) {
              throw throwable;
          }
          catch (Throwable throwable) {
              throw new UndeclaredThrowableException(throwable);
          }
      }
  
      public final int hashCode() {
          try {
              return (Integer)this.h.invoke(this, m0, null);
          }
          catch (Error | RuntimeException throwable) {
              throw throwable;
          }
          catch (Throwable throwable) {
              throw new UndeclaredThrowableException(throwable);
          }
      }
  
      public final void sell() {
          try {
              this.h.invoke(this, m3, null);
              return;
          }
          catch (Error | RuntimeException throwable) {
              throw throwable;
          }
          catch (Throwable throwable) {
              throw new UndeclaredThrowableException(throwable);
          }
      }
  }
  ```

  从上面的类中，我们可以看到以下几个信息：

  * 代理类（$Proxy0）实现了SellTickets。这也就印证了我们之前说的真实类和代理类实现同样的接口。
  * 代理类（$Proxy0）将我们提供了的匿名内部类对象传递给了父类。

* 动态代理的执行流程是什么样？

  下面是摘取的重点代码：

  ```java
  //程序运行过程中动态生成的代理类
  public final class $Proxy0 extends Proxy implements SellTickets {
      private static Method m3;
  	
      //有参构造方法 传入的是invocationHandler接口实现对象
      public $Proxy0(InvocationHandler invocationHandler) {
          super(invocationHandler);
      }
  
      static {
          m3 = Class.forName("com.itheima.proxy.dynamic.jdk.SellTickets").getMethod("sell", new Class[0]);
      }
  
      public final void sell() {
          //这里的h.invoke方法是ProxyFactory类中执行Proxy.newProxyInstance方法传入的InvocationHandler的invoke =====> h就是InvocationHandler
          this.h.invoke(this, m3, null);
      }
  }
  
  //Java提供的动态代理相关类
  public class Proxy implements java.io.Serializable {
  	protected InvocationHandler h;
  	 
  	protected Proxy(InvocationHandler h) {
          this.h = h;
      }
  }
  
  //代理工厂类
  public class ProxyFactory {
  
      private TrainStation station = new TrainStation();
  
      public SellTickets getProxyObject() {
          SellTickets sellTickets = (SellTickets) Proxy.newProxyInstance(station.getClass().getClassLoader(),
                  station.getClass().getInterfaces(),
                  new InvocationHandler() {
                      
                      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  
                          System.out.println("代理点收取一些服务费用(JDK动态代理方式)");
                          Object result = method.invoke(station, args);
                          return result;
                      }
                  });
          return sellTickets;
      }
  }
  
  
  //测试访问类
  public class Client {
      public static void main(String[] args) {
          //获取代理对象
          ProxyFactory factory = new ProxyFactory();
          SellTickets proxyObject = factory.getProxyObject();
          proxyObject.sell();
      }
  }
  ```


执行流程如下：

  1. 在测试类中通过代理对象调用sell()方法
  2. 根据多态的特性，执行的是内存中动态生成的代理类（$Proxy0）中的sell()方法
  3. 内存中动态生成的代理类（$Proxy0）中的sell()方法中又调用了InvocationHandler接口的子实现类对象的invoke方法
  4. invoke方法通过反射执行了真实对象所属类(TrainStation)中的sell()方法

### 5.1.5 CGLIB动态代理

同样是上面的案例，我们再次使用CGLIB代理实现。

如果没有定义SellTickets接口，只定义了TrainStation(火车站类)。很显然JDK代理是无法使用了，因为JDK动态代理要求必须定义接口，对接口进行代理。

CGLIB是一个功能强大，高性能的代码生成包。它为没有实现接口的类提供代理，为JDK的动态代理提供了很好的补充。

CGLIB是第三方提供的包，所以需要引入jar包的坐标：

```xml
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>2.2.2</version>
</dependency>
```

代码如下：

```java
//火车站
public class TrainStation {

    public void sell() {
        System.out.println("火车站卖票");
    }
}
```

ProxyFactory

```java

//代理工厂
//MethodInterceptor 是Callback的子接口
public class ProxyFactory implements MethodInterceptor {

    private TrainStation target = new TrainStation();
	
    //创建TrainStation 类的代理对象方法
    public TrainStation getProxyObject() {
        //创建Enhancer对象，类似于JDK动态代理的Proxy类，下一步就是设置几个参数
        Enhancer enhancer =new Enhancer();
        //设置父类的字节码对象（父类就是被代理类）
        enhancer.setSuperclass(target.getClass());
        //设置回调函数
        enhancer.setCallback(this);//传入实现了MethodInterceptor方法的对象
        //创建代理对象
        TrainStation obj = (TrainStation) enhancer.create();
        return obj;
    }

    /*
        当调用代理对象的任意一个方法的时候 =>本质调用intercept方法
        intercept方法参数说明：
            o ： 代理对象
            method ： 真实对象中的方法的Method实例
            args ： 实际参数
            methodProxy ：代理对象中的方法的method实例
     */
    public TrainStation intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //相当于增强
        System.out.println("代理点收取一些服务费用(CGLIB动态代理方式)");
        //调用被代理对象的目标方法
        TrainStation result = (TrainStation) methodProxy.invokeSuper(o, args);
        return result;
    }
}


```



```java
//测试类
public class Client {
    public static void main(String[] args) {
        //创建代理工厂对象
        ProxyFactory factory = new ProxyFactory();
        //获取代理对象
        TrainStation proxyObject = factory.getProxyObject();

        proxyObject.sell();
    }
}
```

尚硅谷案例：

代理模式(Proxy Pattern) ,给某一个对象提供一个代理，并由代理对象控制对原对象的引用,对象结构型模式。这种也是静态代理

代理模式包含如下角色：
Subject: 抽象主体角色(抽象类或接口)
Proxy: 代理主体角色(代理对象类)
RealSubject: 真实主体角色(被代理对象类)

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片4.png)

动态代理：

```java
public interface SellTikTok {
    void sell();
}
```



```java
//TODO 抽象主体 被代理角色
public  interface ManTikTok {
   void tiktok();
}
```



```java
//TODO 主体
public class LeiTikTok  implements ManTikTok,SellTikTok {
//    @Override
    public void tiktok() {
        System.out.println("雷丰阳，tiktok.... ");
    }

    @Override
    public void sell() {
        System.out.println("雷丰阳，只要666，赶紧来包...");
    }

    public void haha(){
        System.out.println("hahaha ,......");
    }
}
```



```java
/**
 * 敲10遍
 * @param <T>
 */
public class JdkTiktokProxy<T> implements InvocationHandler {
    //TODO 被代理对象 作为成员变量
    private T target;
    //TODO 初始化接受被代理对象
    JdkTiktokProxy(T target){
        this.target = target;
    }
    /**
     * TODO 获取被代理对象的  代理对象
     */
    public static<T> T getProxy(T t) {

        /*TODO
         * ClassLoader loader, 当前被代理对象的类加载器
         * Class<?>[] interfaces, 当前被代理对象所实现的所有接口
         * InvocationHandler h, 当前被代理对象执行目标方法的时候我们使用h可以定义拦截增强方法
         */
        Object o = Proxy.newProxyInstance(
                t.getClass().getClassLoader(),
                t.getClass().getInterfaces(), //TODO 必须接口  因为接口规定了能做什么
                new JdkTiktokProxy(t));//TODO 把对象本身传入
        return (T)o;
    }
    /**
     * TODO 定义目标方法的拦截逻辑；每个方法都会进来的
     */
    @Override
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {

        //反射执行
        System.out.println("真正执行被代理对象的方法");//TODO 增强
        Object invoke = method.invoke(target, args);
        System.out.println("返回值：一堆美女");//TODO 增强
        return invoke;
    }
}
```



```java
/**
 * TODO 动态代理模式：
 *  JDK要求被代理对象必须有接口
 *  代理对象和目标对象的相同点在于都是同一个接口
 */
public class MainTest {
    public static void main(String[] args) {
        ManTikTok leiTikTok = new LeiTikTok();
        // 动态代理机制。
        ManTikTok proxy = JdkTiktokProxy.getProxy(leiTikTok);
        proxy.tiktok();

        ((SellTikTok) proxy).sell();

        //TODO 能不能代理被代理对象本类自己的方法? =》不能 proxy只能转成接口类
//        ((LeiTikTok)proxy).haha();
        System.out.println(Arrays.asList(proxy.getClass().getInterfaces()));

    }
}
```

MainTest

```java
/**
 * 代理的东西不一样，每一种不同的被代理类Person、Dog、Cat,创建不同的静态代理类
 */
public class MainTest {

    public static void main(String[] args) {
        LiMingTiktokProxy proxy = new LiMingTiktokProxy(new LeiTikTok());
        proxy.tiktok();
        //静态代理就是装饰器
        //装饰器模式是代理模式的一种
    }
}
```

CGLIB方式：

LeiTikTok 

```java
/**
 * Subject  主体
 */
public class LeiTikTok  {
    public void tiktokHaha() {
        System.out.println("雷丰阳，tiktok.... haha....");
    }
}
```

 CglibProxy 

```java
/**
 *TODO 1、使用cglib帮我们创建出代理对象
 * 通过继承 得到被代理对象的方法
 */
public class CglibProxy {

    //TODO 为任意对象创建代理
    public static<T> T createProxy(T t){
        //TODO 1、创建一个增强器
        Enhancer enhancer = new Enhancer();

        //TODO 2、设置要增强哪个个类的功能。增强器为这个类动态创建一个子类
        enhancer.setSuperclass(t.getClass());

        //TODO 3、设置回调
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method,  //TODO 为了能获取到原方法的一些元数据信息（签名）
                                    Object[] args,
                                    //TODO 该方法传入一个方法的代理 也就是方法
                                    MethodProxy proxy) throws Throwable {
                //TODO 编写拦截的逻辑 可以理解为前置通知
                System.out.println("cglib上场le .......xxx");
                //TODO获取到原方法的一些元数据信息（签名）
                Class<? extends Method> aClass = method.getClass();
                System.out.println(aClass);
                //TODO 目标方法进行执行  invoke不能调用
                Object invoke = proxy.invokeSuper(obj,args);
                //TODO 返回执行的返回值
                return invoke;
            }
        });
        //TODO 创建代理对象
        Object o = enhancer.create();

        return (T) o;
    }
}
```

 CglibTest

```java
/**
 *         <dependency>
 *             <groupId>cglib</groupId>
 *             <artifactId>cglib</artifactId>
 *             <version>3.3.0</version>
 *         </dependency>
 */
public class CglibTest {

    public static void main(String[] args) {

        //原来的对象都不用new
        LeiTikTok tikTok = new LeiTikTok();

        LeiTikTok proxy = CglibProxy.createProxy(tikTok);

        proxy.tiktokHaha();
    }
}
```

### 5.1.6 三种代理的对比

* jdk代理和CGLIB代理

  使用CGLib实现动态代理，CGLib底层采用ASM字节码生成框架，使用字节码技术生成代理类，在JDK1.6之前比使用Java反射效率要高。唯一需要注意的是，CGLib不能对声明为final的类或者方法进行代理，因为CGLib原理是动态生成被代理类的子类。

  在JDK1.6、JDK1.7、JDK1.8逐步对JDK动态代理优化之后，在调用次数较少的情况下，JDK代理效率高于CGLib代理效率，只有当进行大量调用的时候，JDK1.6和JDK1.7比CGLib代理效率低一点，但是到JDK1.8的时候，JDK代理效率高于CGLib代理。所以如果有接口使用JDK动态代理，如果没有接口使用CGLIB代理。

* 动态代理和静态代理

  动态代理与静态代理相比较，最大的好处是接口中声明的所有方法都被转移到调用处理器一个集中的方法中处理（InvocationHandler.invoke）。这样，在接口方法数量比较多的时候，我们可以进行灵活处理，而不需要像静态代理那样每一个方法进行中转。

  如果接口增加一个方法，静态代理模式除了所有实现类需要实现这个方法外，所有代理类也需要实现此方法。增加了代码维护的复杂度。而动态代理不会出现该问题



### 5.1.7 优缺点

**优点：**

- 代理模式在客户端与目标对象之间起到一个中介作用和保护目标对象的作用；
- 代理对象可以扩展目标对象的功能；
- 代理模式能将客户端与目标对象分离，在一定程度上降低了系统的耦合度；

**缺点：**

* 增加了系统的复杂度；



### 5.1.8 使用场景 

* 远程（Remote）代理

  本地服务通过网络请求远程服务。为了实现本地到远程的通信，我们需要实现网络通信，处理其中可能的异常。为良好的代码设计和可维护性，我们将网络通信部分隐藏起来，只暴露给本地服务一个接口，通过该接口即可访问远程服务提供的功能，而不必过多关心通信部分的细节。

* 防火墙（Firewall）代理

  当你将浏览器配置成使用代理功能时，防火墙就将你的浏览器的请求转给互联网；当互联网返回响应时，代理服务器再把它转给你的浏览器。

* 保护（Protect or Access）代理

  控制对一个对象的访问，如果需要，可以给不同的用户提供不同级别的使用权限。



## 5.2 适配器模式

### 5.2.1 概述

如果去欧洲国家去旅游的话，他们的插座如下图最左边，是欧洲标准。而我们使用的插头如下图最右边的。因此我们的笔记本电脑，手机在当地不能直接充电。所以就需要一个插座转换器，转换器第1面插入当地的插座，第2面供我们充电，这样使得我们的插头在当地能使用。生活中这样的例子很多，手机充电器（将220v转换为5v的电压），读卡器等，其实就是使用到了适配器模式。

![](img\转接头.png)

**定义：**

​	将一个类的接口转换成客户希望的另外一个接口，使得原本由于接口不兼容而不能一起工作的那些类能一起工作。

​	适配器模式分为类适配器模式和对象适配器模式，前者类之间的耦合度比后者高，且要求程序员了解现有组件库中的相关组件的内部结构，所以应用相对较少些。

### 5.2.2 结构

适配器模式（Adapter）包含以下主要角色：

* 目标（Target）接口：当前系统业务所期待的接口，它可以是抽象类或接口。
* 适配者（Adaptee）类：它是被访问和适配的现存组件库中的组件接口。
* 适配器（Adapter）类：**它是一个转换器，通过继承或引用适配者的对象，把适配者接口转换成目标接口，让客户按目标接口的格式访问适配者。**

### 5.2.3 类适配器模式

实现方式：定义一个适配器类来实现当前系统的业务接口，同时又继承现有组件库中已经存在的组件。

【例】读卡器

**现有一台电脑只能读取SD卡，而要读取TF卡中的内容的话就需要使用到适配器模式。**创建一个读卡器，将TF卡中的内容读取出来。

类图如下：

<img src="img/适配器模式.png" style="zoom:80%;" />

代码如下：

```java
/**
 * @version v1.0
 * @ClassName: Computer
 * @Description: 计算机类
 * @Author: 黑马程序员
 */
public class Computer {

    //从SD卡中读取数据
    public String readSD(SDCard sdCard) {
        if(sdCard == null) {
            throw  new NullPointerException("sd card is not null");
        }
        return sdCard.readSD();
    }
}
```

SDCard

```java
/**
 * @version v1.0
 * @ClassName: SDCard
 * @Description: 目标接口
 * @Author: 黑马程序员
 */
public interface SDCard {

    //从SD卡中读取数据
    String readSD();
    //往SD卡中写数据
    void writeSD(String msg);
}
```

SDCardImpl

```java
/**
 * @version v1.0
 * @ClassName: SDCardImpl
 * @Description: 具体的SD卡
 * @Author: 黑马程序员
 */
public class SDCardImpl implements SDCard {

    public String readSD() {
        String msg = "SDCard read msg ： hello word SD";
        return msg;
    }

    public void writeSD(String msg) {
        System.out.println("SDCard write msg ：" + msg);
    }
}
```

TFCard

```java
/**
 * @version v1.0
 * @ClassName: TFCard
 * @Description: 适配者类的接口
 * @Author: 黑马程序员
 */
public interface TFCard {

    //从TF卡中读取数据
    String readTF();
    //往TF卡中写数据
    void writeTF(String msg);
}
```

TFCardImpl

```java
/**
 * @version v1.0
 * @ClassName: TFCardImpl
 * @Description: 适配者类
 * @Author: 黑马程序员
 */
public class TFCardImpl implements TFCard {

    public String readTF() {
        String msg = "TFCard read msg ： hello word TFcard";
        return msg;
    }

    public void writeTF(String msg) {
        System.out.println("TFCard write msg :" + msg);
    }
}
```

SDAdapterTF

```java
/**
 * @version v1.0
 * @ClassName: SDAdapterTF
 * @Description: 适配器类
 * @Author: 黑马程序员
 */
public class SDAdapterTF extends TFCardImpl implements SDCard {

    public String readSD() {
        System.out.println("adapter read tf card");
        return readTF();
    }

    public void writeSD(String msg) {
        System.out.println("adapter write tf card");
        writeTF(msg);
    }
}

```

Client

```java
/**
 * @version v1.0
 * @ClassName: Client
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class Client {
    public static void main(String[] args) {
        //创建计算机对象
        Computer computer = new Computer();
        //读取SD卡中的数据
        String msg = computer.readSD(new SDCardImpl());
        System.out.println(msg);

        System.out.println("===============");
        //使用该电脑读取TF卡中的数据
        //定义适配器类
        String msg1 = computer.readSD(new SDAdapterTF());
        System.out.println(msg1);
    }
}
```

类适配器模式违背了合成复用原则。类适配器是客户类有一个接口规范的情况下可用，反之不可用。



### 5.2.4 对象适配器模式

实现方式：对象适配器模式可釆用将现有组件库中已经实现的组件引入适配器类中，该类同时实现当前系统的业务接口。

【例】读卡器

我们使用对象适配器模式将读卡器的案例进行改写。类图如下：

<img src="img\对象适配器模式.png" style="zoom:80%;" />

代码如下：

类适配器模式的代码，我们只需要修改适配器类（SDAdapterTF）和测试类。

```java
/**
 * @version v1.0
 * @ClassName: SDAdapterTF
 * @Description: 适配器类
 * @Author: 黑马程序员
 */
public class SDAdapterTF implements SDCard {

    //声明适配者类
    private TFCard tfCard;

    public SDAdapterTF(TFCard tfCard) {
        this.tfCard = tfCard;
    }

    public String readSD() {
        System.out.println("adapter read tf card");
        return tfCard.readTF();
    }

    public void writeSD(String msg) {
        System.out.println("adapter write tf card");
        tfCard.writeTF(msg);
    }
}
```

 Client

```java
/**
 * @version v1.0
 * @ClassName: Client
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class Client {
    public static void main(String[] args) {
        //创建计算机对象
        Computer computer = new Computer();
        //读取SD卡中的数据
        String msg = computer.readSD(new SDCardImpl());
        System.out.println(msg);

        System.out.println("===============");
        //使用该电脑读取TF卡中的数据
        //创建适配器类对象
        SDAdapterTF sdAdapterTF = new SDAdapterTF(new TFCardImpl());
        String msg1 = computer.readSD(sdAdapterTF);
        System.out.println(msg1);
    }
}
```

> **注意：还有一个适配器模式是接口适配器模式。当不希望实现一个接口中所有的方法时，可以创建一个抽象类Adapter ，实现所有方法。而此时我们只需要继承该抽象类即可。**



尚硅谷案例：

将一个接口转换成客户希望的另一个接口，适配器模式使接口不兼容的那些类可以一起工作，适配器模式分为类结构型模式（继承）和对象结构型模式（组合）两种，前者（继承）类之间的耦合度比后者高，且要求程序员了解现有组件库中的相关组件的内部结构，所以应用相对较少些。

别名也可以是Wrapper，包装器



适配器模式（Adapter）包含以下主要角色。
目标（Target）接口：可以是抽象类或接口。客户希望直接用的接口
适配者（Adaptee）类：隐藏的转换接口
适配器（Adapter）类：它是一个转换器，通过继承或引用适配者的对象，把适配者接口转换成目标接口。

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片1.png)

Player 

```java
/**
 *TODO
 *  1、系统原有接口 player：可以播放电影，并且返回字幕
 *  这个接口的功能需要被扩展
 */
public interface Player {

    String play();
}
```

Translator

```java
/**
 * 2、系统原有接口，可以翻译文字内容
 */
public interface Translator {
    String translate(String content);
}
```

类结构型

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片2.png)

JPMoviePlayerAdapter

```java
/**
 * TODO 继承的方式：类结构模型，适配转换到了翻译器的功能上
 *  继承原有接口player 也继承被适配的接口Zh_JPTranslator
 * TODO
 *  让被适配的对象Player能有新的功能
 */
public class JPMoviePlayerAdapter extends Zh_JPTranslator implements Player {
    //TODO 被适配对象
    private Player target;
    //TODO 构造器 传入被适配的对象
    public JPMoviePlayerAdapter(Player target){
        this.target = target;
    }

    //TODO 给play方法添加功能 相当于 被适配对象的增强
    @Override
    public String play() {
        String play = target.play();
        //转换字幕
        String translate = translate(play);
        System.out.println("日文："+translate);
        return play;
    }
}
```

MainTest

```java
public class MainTest {
    public static void main(String[] args) {

        MoviePlayer player = new MoviePlayer();
        //TODO 创建适配器
        JPMoviePlayerAdapter adapter = new JPMoviePlayerAdapter(player);
        //TODO 适配器调用 play方法
        adapter.play();


    }
}
```

对象结构型

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片3.png)



JPMovieAdapter

```java
/**
 *
 *TODO
 * 1、在原有系统上增加一个适配器。让适配器可以把电影的中文字幕翻译成友人理解的日文字幕
 * 客户调用方法的时候用适配器操作即可。
 * 2种适配器的类型
 *  类结构型模式：
 *  对象结构型模式：
 */
public class JPMovieAdapter implements Player {

    public JPMovieAdapter(){
    }

    @Override
    public String play() {

        return null;
    }
}
```

MoviePlayer 

```java
/**
 * 电影播放器
 * 阅读器
 * ....
 */
public class MoviePlayer implements Player {
    @Override
    public String play() {
        System.out.println("正在播放：宋老师的宝贵时间.avi");
        String content = "你好";
        System.out.println(content);  //并且打印出字幕
        return content;
    }
}
```

Zh_JPTranslator

```java
/**
 * ZH_JP翻译器
 * ZH_EN翻译器
 * .....
 */
public class Zh_JPTranslator implements Translator{
    @Override
    public String translate(String content) {
        if("你好".equals(content)){
            return "空尼几哇";
        }
        if ("什么".equals(content)){
            return "纳尼";
        }
        return "*******";
    }
}
```

MainTest

```java
/**
 *TODO
 * 适配器
 * 1、系统原有两个已存在接口 player、translate没有任何关系
 *      需求，现在一个小....日本友人。看电影字幕是中文的不习惯。
 * 2、我们在不改变原有系统的基础上实现这个功能就需要一个适配器
 *  系统原来存在的所有接口都不能动。扩展一个新的类，来连接两个之前不同的类
 *
 */
public class MainTest {

    public static void main(String[] args) {

        //1、友人想要看电影带日文字幕
        MoviePlayer moviePlayer = new MoviePlayer();
        moviePlayer.play();
        
    }
}
```

### 5.2.5 应用场景

* 以前开发的系统存在满足新系统功能需求的类，但其接口同新系统的接口不一致。
* 使用第三方提供的组件，但组件接口定义和自己要求的接口定义不同。



### 5.2.6 JDK源码解析

Reader（字符流）、InputStream（字节流）的适配使用的是InputStreamReader。

InputStreamReader继承自java.io包中的Reader，对他中的抽象的未实现的方法给出实现。如：

```java
public int read() throws IOException {
    return sd.read();
}

public int read(char cbuf[], int offset, int length) throws IOException {
    return sd.read(cbuf, offset, length);
}
```

如上代码中的sd（StreamDecoder类对象），在Sun的JDK实现中，实际的方法实现是对sun.nio.cs.StreamDecoder类的同名方法的调用封装。类结构图如下：

![](img\适配器模式-jdk源码解析.png)

从上图可以看出：

* InputStreamReader是对同样实现了Reader的StreamDecoder的封装。
* StreamDecoder不是Java SE API中的内容，是Sun  JDK给出的自身实现。但我们知道他们对构造方法中的字节流类（InputStream）进行封装，并通过该类进行了字节流和字符流之间的解码转换。

<font color="red">结论：</font>

​	从表层来看，InputStreamReader做了InputStream字节流类到Reader字符流之间的转换。而从如上Sun JDK中的实现类关系结构中可以看出，是StreamDecoder的设计实现在实际上采用了适配器模式。



## 5.3 装饰者模式

### 5.3.1 概述

我们先来看一个快餐店的例子。

快餐店有炒面、炒饭这些快餐，可以额外附加鸡蛋、火腿、培根这些配菜，当然加配菜需要额外加钱，每个配菜的价钱通常不太一样，那么计算总价就会显得比较麻烦。

<img src="/img/装饰者模式-使用前.png" style="zoom:80%;" />

使用继承的方式存在的问题：

* 扩展性不好

  如果要再加一种配料（火腿肠），我们就会发现需要给FriedRice和FriedNoodles分别定义一个子类。如果要新增一个快餐品类（炒河粉）的话，就需要定义更多的子类。

* 产生过多的子类

**定义：**

​	指在不改变现有对象结构的情况下，动态地给该对象增加一些职责（即增加其额外功能）的模式。

### 5.3.2 结构

装饰（Decorator）模式中的角色：

* 抽象构件（Component）角色 ：定义一个抽象接口以规范准备接收附加责任的对象。
* 具体构件（Concrete  Component）角色 ：实现抽象构件，通过装饰角色为其添加一些职责。
* 抽象装饰（Decorator）角色 ： 继承或实现抽象构件，并包含具体构件的实例，可以通过其子类扩展具体构件的功能。
* 具体装饰（ConcreteDecorator）角色 ：实现抽象装饰的相关方法，并给具体构件对象添加附加的责任。



### 5.3.3 案例

我们使用装饰者模式对快餐店案例进行改进，体会装饰者模式的精髓。

类图如下：

***Garnish的聚合方向画反了***

<img src="img/装饰者模式.png" style="zoom:75%;" />

代码如下：

```java
/**
 * @version v1.0
 * @ClassName: Garnish
 * @Description: 装饰者类(抽象装饰者角色)
 * @Author: 黑马程序员
 */
public abstract class Garnish extends FastFood {

    //声明快餐类的变量 作为组合
    private FastFood fastFood;

    public FastFood getFastFood() {
        return fastFood;
    }

    public void setFastFood(FastFood fastFood) {
        this.fastFood = fastFood;
    }

    public Garnish(FastFood fastFood,float price, String desc) {
        super(price, desc);
        this.fastFood = fastFood;
    }
}
```

 FastFood

```java
/**
 * @version v1.0
 * @ClassName: FastFood
 * @Description: 快餐类(抽象构件角色)
 * @Author: 黑马程序员
 */
public abstract class FastFood {

    private float price;//价格
    private String desc; //描述

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public FastFood(float price, String desc) {
        this.price = price;
        this.desc = desc;
    }

    public FastFood() {
    }

    public abstract float cost();
}
```

Egg

```java
/**
 * @version v1.0
 * @ClassName: Egg
 * @Description: 鸡蛋类(具体的装饰者角色)
 * @Author: 黑马程序员
 */
public class Egg extends Garnish {
	//继承自父类 有三个成员变量
    
    public Egg(FastFood fastFood) {
        super(fastFood,1,"鸡蛋");
    }

    public float cost() {
        //计算价格
        return getPrice() + getFastFood().cost();
    }

    @Override
    public String getDesc() {
        return super.getDesc() + getFastFood().getDesc();
    }
}
```

 Bacon

```java
/**
 * @version v1.0
 * @ClassName: Egg
 * @Description: 培根类(具体的装饰者角色)
 * @Author: 黑马程序员
 */
public class Bacon extends Garnish {
	//继承自父类 有三个成员变量
    
    public Bacon(FastFood fastFood) {
        super(fastFood,2,"培根");
    }

    public float cost() {
        //计算价格
        return getPrice() + getFastFood().cost();
    }

    @Override
    public String getDesc() {
        return super.getDesc() + getFastFood().getDesc();
    }
}
```

FriedNoodles

```java
/**
 * @version v1.0
 * @ClassName: FriedNoodles
 * @Description: 炒面(具体的构件角色)
 * @Author: 黑马程序员
 */
public class FriedNoodles extends FastFood {

    public FriedNoodles() {
        super(12,"炒面");
    }

    public float cost() {
        return getPrice();
    }
}
```

 FriedRice

```java
/**
 * @version v1.0
 * @ClassName: FriedRice
 * @Description: 炒饭(具体构件角色)
 * @Author: 黑马程序员
 */
public class FriedRice extends FastFood {

    public FriedRice() {
        super(10,"炒饭");
    }

    public float cost() {
        return getPrice();
    }
}
```

 Client 

```java

//测试类
public class Client {
    public static void main(String[] args) {
        //点一份炒饭
        FastFood food = new FriedRice();
        //花费的价格
        System.out.println(food.getDesc() + " " + food.cost() + "元");

        System.out.println("========");
        //点一份加鸡蛋的炒饭
        FastFood food1 = new FriedRice();
		//增强（装饰）
        food1 = new Egg(food1);
        //花费的价格
        System.out.println(food1.getDesc() + " " + food1.cost() + "元");

        System.out.println("========");
        //点一份加培根的炒面
        FastFood food2 = new FriedNoodles();
        //增强（装饰）
        food2 = new Bacon(food2);
        //花费的价格
        System.out.println(food2.getDesc() + " " + food2.cost() + "元");
    }
}
```

聚合后继承的目的在于继承抽象构件的成员属性，同时保证cast方法调用并重写计算总价，而不是另起方法来实现，节约代码，但是完全也可以通过桥接模式来实现，两者相比肯定是装饰者模式更简单

尚硅谷案例：

适配器是连接两个类，可以增强一个类，装饰器是增强一个类
向一个现有的对象添加新的功能，同时又不改变其结构。属于对象结构型模式。
创建了一个装饰类，用来包装原有的类，并在保持类方法签名完整性的前提下，提供了额外的功能。

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片5.png)

抽象构件（Component）角色：

- 定义一个抽象接口以规范准备接收附加责任的对象。

具体构件（ConcreteComponent）角色：

- 实现抽象构件，通过装饰角色为其添加一些职责。

抽象装饰（Decorator）角色：

- 继承抽象构件，并包含具体构件的实例，可以通过其子类扩展具体构件的功能。

具体装饰（ConcreteDecorator）角色：

- 实现抽象装饰的相关方法，并给具体构件对象添加附加的责任。

ManTikTok

```java
//TODO 抽象构建
public  interface ManTikTok {
   void tiktok();
}
```

TiktokDecorator

```java
/**
 * 抽象装饰器
 * 抖音直播装饰器
 */
public interface TiktokDecorator extends ManTikTok{
    //TODO 给原有接口 做增强 多了个方法
    void enable();
}
```

LeiTikTok

```java
public class LeiTikTok implements ManTikTok{
    //TODO
    @Override
    public void tiktok() {
        System.out.println("雷丰阳，tiktok.... ");
    }
}
```

 ZhangTikTok 

```java
public class ZhangTikTok implements ManTikTok{
    @Override
    public void tiktok() {
        System.out.println("张三，tiktok.... ");
    }
}
```

MeiYanDecorator

```java
/**
 *TODO  装饰器只关系增强的这个类的方法。
 * 如果增强的功能是其他类 而不是自己实现的话 引入其他类 就会变成适配器模式
 */
public class MeiYanDecorator implements TiktokDecorator{
    //TODO 定义被装饰的对象 为成员变量
    private ManTikTok manTikTok;
    //TODO 传入被装饰的对象
    public MeiYanDecorator(ManTikTok manTikTok){
        this.manTikTok = manTikTok;
    }

    @Override
    public void tiktok() {
        //TODO 增强功能 例如：开启美颜
        enable();
        //TODO 执行被被增强的方法 我开始直播
        manTikTok.tiktok();
    }

    //TODO 定义的增强功能
    @Override
    public void enable() {
        System.out.println("看这个美女.....");
        System.out.println("花花花花花花花花花花花");
    }
}
```

MainTest

```java
/**
 * 核心：想要不改变原来接口方法的情况下扩展新功能，或者增强方法.....
 */
public class MainTest {

    public static void main(String[] args) {
        //被装饰对象
        ManTikTok manTikTok = new LeiTikTok();
//        manTikTok.tiktok();
        //TODO 传入被装饰的对象 这里和适配器类似
        MeiYanDecorator decorator = new MeiYanDecorator(manTikTok);
        decorator.tiktok();
    }
}
```

**好处：**

* 饰者模式可以带来比继承更加灵活性的扩展功能，使用更加方便，可以通过组合不同的装饰者对象来获取具有不同行为状态的多样化的结果。装饰者模式比继承更具良好的扩展性，完美的遵循开闭原则，继承是静态的附加责任，装饰者则是动态的附加责任。

* 装饰类和被装饰类可以独立发展，不会相互耦合，装饰模式是继承的一个替代模式，装饰模式可以动态扩展一个实现类的功能。



### 5.3.4 使用场景

* 当不能采用继承的方式对系统进行扩充或者采用继承不利于系统扩展和维护时。

  不能采用继承的情况主要有两类：

  * 第一类是系统中存在大量独立的扩展，为支持每一种组合将产生大量的子类，使得子类数目呈爆炸性增长；
  * 第二类是因为类定义不能继承（如final类）

* 在不影响其他对象的情况下，以动态、透明的方式给单个对象添加职责。

* 当对象的功能要求可以动态地添加，也可以再动态地撤销时。



### 5.3.5 JDK源码解析

IO流中的包装类使用到了装饰者模式。BufferedInputStream，BufferedOutputStream，BufferedReader，BufferedWriter。

我们以BufferedWriter举例来说明，先看看如何使用BufferedWriter

```java
public class Demo {
    public static void main(String[] args) throws Exception{
        //创建BufferedWriter对象
        //创建FileWriter对象
        FileWriter fw = new FileWriter("C:\\Users\\Think\\Desktop\\a.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        //写数据
        bw.write("hello Buffered");

        bw.close();
    }
}
```

使用起来感觉确实像是装饰者模式，接下来看它们的结构：

<img src="img\装饰者模式-jdk源码.png" style="zoom:80%;" />

> <font color="red">小结：</font>
>
> ​	**BufferedWriter使用装饰者模式对Writer子实现类进行了增强，添加了缓冲区**，提高了写数据的效率。



### 5.3.6 代理和装饰者的区别

静态代理和装饰者模式的区别：

* 相同点：
  * 都要实现与目标类相同的业务接口
  * 在两个类中都要声明目标对象
  * 都可以在不修改目标类的前提下增强目标方法
* 不同点：
  * 目的不同
    装饰者是为了增强目标对象
    静态代理是为了保护和隐藏目标对象
  * 获取目标对象构建的地方不同
    装饰者是由外界传递进来，可以通过构造方法传递
    静态代理是在代理类内部创建，以此来隐藏目标对象



## 5.4 桥接模式

### 5.4.1 概述

现在有一个需求，需要创建不同的图形，并且每个图形都有可能会有不同的颜色。我们可以利用继承的方式来设计类的关系：

![](img\image-20200207194617620.png)

我们可以发现有很多的类，假如我们再增加一个形状或再增加一种颜色，就需要创建更多的类。

试想，在一个有多种可能会变化的维度的系统中，用继承方式会造成类爆炸，扩展起来不灵活。每次在一个维度上新增一个具体实现都要增加多个子类。为了更加灵活的设计系统，我们此时可以考虑使用桥接模式。

**定义：**

​	**将抽象与实现分离，使它们可以独立变化。它是用组合关系代替继承关系来实现，从而降低了抽象和实现这两个可变维度（形状/维度）的耦合度。**



### 5.4.2 结构

桥接（Bridge）模式包含以下主要角色：

* 抽象化（Abstraction）角色 ：定义抽象类，并包含一个对实现化对象的引用。
* 扩展抽象化（Refined  Abstraction）角色 ：是抽象化角色的子类，实现父类中的业务方法，并通过组合关系调用实现化角色中的业务方法。
* 实现化（Implementor）角色 ：定义实现化角色的接口，供扩展抽象化角色调用。
* 具体实现化（Concrete Implementor）角色 ：给出实现化角色接口的具体实现。



### 5.4.3 案例

【例】视频播放器

需要开发一个跨平台视频播放器，可以在不同操作系统平台（如Windows、Mac、Linux等）上播放多种格式的视频文件，常见的视频格式包括RMVB、AVI、WMV等。**该播放器包含了两个维度（操作系统/编码格式）**，适合使用桥接模式。

类图如下：

<img src="img\桥接模式.png" style="zoom:80%;" />

代码如下：

```java
/**
 * @version v1.0
 * @ClassName: VideoFile
 * @Description: 视频文件(实现化角色)
 * @Author: 黑马程序员
 */
public interface VideoFile {

    //解码功能
    void decode(String fileName);
}
```

AviFile

```java
/**
 * @version v1.0
 * @ClassName: AviFile
 * @Description: avi视频文件（具体的实现化角色）
 * @Author: 黑马程序员
 */
public class AviFile implements VideoFile {

    public void decode(String fileName) {
        System.out.println("avi视频文件 ：" + fileName);
    }
}
```

RmvbFile

```java
/**
 * @version v1.0
 * @ClassName: RmvbFile
 * @Description: rmvb视频文件（具体的实现化角色）
 * @Author: 黑马程序员
 */
public class RmvbFile implements VideoFile {

    public void decode(String fileName) {
        System.out.println("rmvb视频文件 ：" + fileName);
    }
}
```

OpratingSystem

```java
/**
 * @version v1.0
 * @ClassName: OpratingSystem
 * @Description: 抽象的操作系统类(抽象化角色)
 * @Author: 黑马程序员
 */
public abstract class OpratingSystem {

    //声明videFile变量
    protected VideoFile videoFile;

    public OpratingSystem(VideoFile videoFile) {
        this.videoFile = videoFile;
    }

    public abstract void play(String fileName);
}
```

Mac

```java
/**
 * @version v1.0
 * @ClassName: Mac
 * @Description: Mac操作系统(扩展抽象化角色)
 * @Author: 黑马程序员
 */
public class Mac extends OpratingSystem {

    public Mac(VideoFile videoFile) {
        super(videoFile);
    }

    public void play(String fileName) {
        this.videoFile.decode(fileName);
    }
}
```

 Windows

```java
/**
 * @version v1.0
 * @ClassName: Windows
 * @Description: 扩展抽象化角色(windows操作系统)
 * @Author: 黑马程序员
 */
public class Windows extends OpratingSystem {

    public Windows(VideoFile videoFile) {
        super(videoFile);
    }

    public void play(String fileName) {
        this.videoFile.decode(fileName);
    }
}
```

OpratingSystem

```java
/**
 * @version v1.0
 * @ClassName: OpratingSystem
 * @Description: 抽象的操作系统类(抽象化角色)
 * @Author: 黑马程序员
 */
public abstract class OpratingSystem {

    //声明videFile变量
    protected VideoFile videoFile;

    public OpratingSystem(VideoFile videoFile) {
        this.videoFile = videoFile;
    }

    public abstract void play(String fileName);
}
```

 Client 

```java

/**
 * @version v1.0
 * @ClassName: Client
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class Client {
    public static void main(String[] args) {
        //创建mac系统对象
        OpratingSystem system = new Mac(new AviFile());
        //使用操作系统播放视频文件
        system.play("战狼3");
    }
}
```

尚硅谷案例：

将抽象与实现解耦，使两者都可以独立变化

在现实生活中，某些类具有两个或多个维度的变化，如图形既可按形状分，又可按颜色分。如何设计类似于 Photoshop 这样的软件，能画不同形状和不同颜色的图形呢？如果用继承方式，m 种形状和 n 种颜色的图形就有 m×n 种，不但对应的子类很多，而且扩展困难。不同颜色和字体的文字、不同品牌和功率的汽车

桥接将继承转为关联，降低类之间的耦合度，减少代码量

| **商品       渠道** | **电商专供** | **线下销售** |
| ------------------- | ------------ | ------------ |
| 拍照手机            | 拍照、电商   | 拍照、线下   |
| 性能手机            | 性能、电商   | 性能、线下   |

桥接（Bridge）模式包含以下主要角色。

- 系统设计期间，如果这个类里面的一些东西，会扩展很多，这个东西就应该分离出来
- 抽象化（Abstraction）角色：定义抽象类，并包含一个对实现化对象的引用。
- 扩展抽象化（Refined Abstraction）角色：是抽象化角色的子类，实现父类中的业务方法，并通过组合关系调用实现化角色中的业务方法。
- 实现化（Implementor）角色：定义实现化角色的接口，供扩展抽象化角色调用。

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片6.png)

AbstractPhone 

```java
/**
 *TODO 1、抽象手机类
 *  手机有各种销售渠道价格都不一样
 */
public abstract class AbstractPhone {
    /*
    * TODO
    *  桥接在此.....设计期间就得想好
    *  【真正会引起此类变化的一个维度直接抽取出来，通过组合的方式接起来】
    */
    AbstractSale sale; //TODO 分离渠道【桥接的关注点】

    /**
     * 当前手机的描述
     */
    abstract String getPhone();

    public void setSale(AbstractSale sale) {
        this.sale = sale;
    }
}
```

AbstractSale

```java
/**
 * TODO 抽象销售渠道
 *  PhoneOnSale  ==howToSale
 *  PhoneOffSale == howToSale
 *  PhoneStudentSale = howToSale
 *  PhonePDD == howToSale
 */
public abstract class AbstractSale {
    //TODO 渠道类型
    private String type;
    //TODO 渠道价格
    private Integer price;

    public AbstractSale(String type,Integer price){
        this.type = type;
        this.price = price;
    }

    String getSaleInfo(){
        return "渠道："+type+"==>"+"价格："+price;
    }

    void howToSale(){
        //都不一样
    }
}
```

IPhone 

```java
public class IPhone extends AbstractPhone{

    @Override
    String getPhone() {
        return "IPhone："+sale.getSaleInfo();
    }
}
```

MiPhone

```java
public class MiPhone extends AbstractPhone{
    @Override
    String getPhone() {
        return "小米：";
    }
}
```

OfflineSale

```java
//线TODO 下渠道
public class OfflineSale  extends AbstractSale{
    public OfflineSale(String type, Integer price) {
        super(type, price);
    }
}
```

OnlineSale

```java
//TODO 线上渠道
public class OnlineSale extends AbstractSale{
    public OnlineSale(String type, Integer price) {
        super(type, price);
    }
}
```

StudentSale

```java
//学生渠道
public class StudentSale extends AbstractSale{
    public StudentSale(String type, Integer price) {
        super(type, price);
    }
}
```

**好处：**

* 桥接模式提高了系统的可扩充性，在两个变化维度中任意扩展一个维度，都不需要修改原有系统。

  如：如果现在还有一种视频文件类型wmv，我们只需要再定义一个类实现VideoFile接口即可，其他类不需要发生变化。

* 实现细节对客户透明

### 5.4.4 使用场景

* 当一个类存在两个独立变化的维度，且这两个维度都需要进行扩展时。
* 当一个系统不希望使用继承或因为多层次继承导致系统类的个数急剧增加时。
* 当一个系统需要在构件的抽象化角色和具体化角色之间增加更多的灵活性时。避免在两个层次之间建立静态的继承联系，通过桥接模式可以使它们在抽象层建立一个关联关系(聚合关系)。



## 5.5 外观模式

### 5.5.1 概述

有些人可能炒过股票，但其实大部分人都不太懂，这种没有足够了解证券知识的情况下做股票是很容易亏钱的，刚开始炒股肯定都会想，如果有个懂行的帮帮手就好，其实基金就是个好帮手，支付宝里就有许多的基金，它将投资者分散的资金集中起来，交由专业的经理人进行管理，投资于股票、债券、外汇等领域，而基金投资的收益归持有者所有，管理机构收取一定比例的托管管理费用。

**定义：**

​	又名门面模式，是一种通过为多个复杂的子系统提供一个一致的接口，而使这些子系统更加容易被访问的模式。该模式对外有一个统一接口，外部应用程序不用关心内部子系统的具体的细节，这样会大大降低应用程序的复杂度，提高了程序的可维护性。

​	外观（Facade）模式是“迪米特法则”的典型应用

![](img\外观模式引入.jpg)



### 5.5.2 结构

外观（Facade）模式包含以下主要角色：

* 外观（Facade）角色：为多个子系统对外提供一个共同的接口。
* 子系统（Sub System）角色：实现系统的部分功能，客户可以通过外观角色访问它。



### 5.5.3 案例

【例】智能家电控制

小明的爷爷已经60岁了，一个人在家生活：每次都需要打开灯、打开电视、打开空调；睡觉时关闭灯、关闭电视、关闭空调；操作起来都比较麻烦。所以小明给爷爷买了智能音箱，可以通过语音直接控制这些智能家电的开启和关闭。类图如下：

<img src="img/外观模式.png" style="zoom:80%;" />

代码如下：

 Light

```java
/**
 * @version v1.0
 * @ClassName: Light
 * @Description: 电灯类
 * @Author: 黑马程序员
 */
public class Light {

    //开灯
    public void on() {
        System.out.println("打开电灯。。。。");
    }

    //关灯
    public void off() {
        System.out.println("关闭电灯。。。。");
    }
}
```

AirCondition

```java
/**
 * @version v1.0
 * @ClassName: TV
 * @Description: 空调类
 * @Author: 黑马程序员
 */
public class AirCondition {
    public void on() {
        System.out.println("打开空调。。。。");
    }

    public void off() {
        System.out.println("关闭空调。。。。");
    }
}
```

TV

```java
/**
 * @version v1.0
 * @ClassName: TV
 * @Description: 电视机类
 * @Author: 黑马程序员
 */
public class TV {
    public void on() {
        System.out.println("打开电视机。。。。");
    }

    public void off() {
        System.out.println("关闭电视机。。。。");
    }
}
```

SmartAppliancesFacade

```java
/**
 * @version v1.0
 * @ClassName: SmartAppliancesFacade
 * @Description: 外观类，用户主要和该类对象进行交互
 * @Author: 黑马程序员
 */
public class SmartAppliancesFacade {

    //聚合电灯对象，电视机对象，空调对象
    private Light light;
    private TV tv;
    private AirCondition airCondition;

    public SmartAppliancesFacade() {
        light = new Light();
        tv = new TV();
        airCondition = new AirCondition();
    }

    //通过语言控制
    public void say(String message) {
        if(message.contains("打开")) {
            on();
        } else if(message.contains("关闭")) {
            off();
        } else {
            System.out.println("我还听不懂你说的！！！");
        }
    }

    //一键打开功能
    private void on() {
        light.on();
        tv.on();
        airCondition.on();
    }

    //一键关闭功能
    private void off() {
        light.off();
        tv.off();
        airCondition.off();
    }
}
```

Client

```java

//测试类
public class Client {
    public static void main(String[] args) {
        //创建外观对象
        SmartAppliancesFacade facade = new SmartAppliancesFacade();
        //客户端直接与外观对象进行交互
        facade.say("打开家电");
        facade.say("关闭家电");
    }
}
```

尚硅谷案例：

外观（Facade）模式又叫作门面模式，是一种通过为多个复杂的子系统提供一个一致的接口，而使这些子系统更加容易被访问的模式

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day03\img\图片7.png)

Edu

```java
public class Edu {
    public void assignSchool(String name){
        System.out.println(name+"，你的孩子明天去 硅谷大学附属幼儿园 报道......");
    }
}
```

Police

```java
public class Police {
    public void resgister(String name){
        System.out.println(name + "已办理落户");
    }
}
```

Social

```java
public class Social {
    public void handleSocial(String name){
        System.out.println(name+"，你的社保关系已经转移....");
    }
}
```

WeiXinFacade

```java
public class WeiXinFacade {
    Police police = new Police();
    Edu edu = new Edu();
    Social social = new Social();
    
    /**
     * 封装起来只留一个方法
     * @param name
     */
    public void handleXxx(String name){
        police.resgister(name);
        edu.assignSchool(name);
        social.handleSocial(name);
    }

    public void resgister(String name){
        police.resgister(name);
    }
    public void assignSchool(String name){
        edu.assignSchool(name);
    }

}
```

MainTest

```java
/**
 * 需求：来回跑太麻烦，按照最少知道原则，我就想和一个部门进行交互。
 */
public class MainTest {

    public static void main(String[] args) {
        //TODO 调用了3个方法
        Police police = new Police();
        police.resgister("雷丰阳");

        Edu edu = new Edu();
        edu.assignSchool("雷丰阳");

        Social social = new Social();
        social.handleSocial("雷丰阳");

        WeiXinFacade facade = new WeiXinFacade();

        facade.handleXxx("雷丰阳");
//
//        facade.resgister("");
//        facade.assignSchool("");

    }
}
```

**好处：**

* 降低了子系统与客户端之间的耦合度，使得子系统的变化不会影响调用它的客户类。
* 对客户屏蔽了子系统组件，减少了客户处理的对象数目，并使得子系统使用起来更加容易。

**缺点：**

* 不符合开闭原则，修改很麻烦



### 5.5.4 使用场景

* 对分层结构系统构建时，使用外观模式定义子系统中每层的入口点可以简化子系统之间的依赖关系。
* 当一个复杂系统的子系统很多时，外观模式可以为系统设计一个简单的接口供外界访问。
* 当客户端与多个子系统之间存在很大的联系时，引入外观模式可将它们分离，从而提高子系统的独立性和可移植性。



### 5.5.5 源码解析

使用tomcat作为web容器时，接收浏览器发送过来的请求，tomcat会将请求信息封装成ServletRequest对象，如下图①处对象。但是大家想想ServletRequest是一个接口，它还有一个子接口HttpServletRequest，而我们知道该request对象肯定是一个HttpServletRequest对象的子实现类对象，到底是哪个类的对象呢？可以通过输出request对象，我们就会发现是一个名为RequestFacade的类的对象。

<img src="img/image-20200207234545691.png" style="zoom:60%;" />

**RequestFacade类就使用了外观模式**。先看结构图：

**Request作为子系统的角色 聚合到了RequestFacade**

<img src="img/外观模式-jdk源码解析.png" style="zoom:70%;" />

**为什么在此处使用外观模式呢？**

​	定义 RequestFacade 类，分别实现 ServletRequest ，同时定义私有成员变量 Request ，并且方法的实现调用 Request  的实现。然后，将 RequestFacade上转为 ServletRequest  传给 servlet 的 service 方法，这样即使在 servlet 中被下转为 RequestFacade ，也不能访问私有成员变量对象中的方法。既用了 Request ，又能防止其中方法被不合理的访问。

