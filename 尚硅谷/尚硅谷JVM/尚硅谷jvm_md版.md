













































## 第8篇-JVM性能调优案例篇

### 1-概述篇



#### 1-生产环境中的问题

1.   生产环境发生了内存溢出该如何处理？
2.   生产环境应该给服务器分配多少内存合适？
3.   如何对垃圾回收器的性能进行调优？
4.   生产环境CPU负载飙高该如何处理？
5.   生产环境应该给应用分配多少线程合适？
6.   不加log，如何确定请求是否执行了某一行代码？
7.   不加log，如何实时查看某个方法的入参与返回值？



#### 2-调优基本问题

-   1-为什么要调优？
  - ​    防止出现OOM，进行JVM规划和预调优
  - ​    解决程序运行中各种OOM
  - ​    减少Full GC出现的频率，解决运行慢、卡顿问题
-   2-调优的大方向
  - ​    合理地编写代码
  - ​    充分并合理的使用硬件资源
  - ​    合理地进行JVM调优
-   3-不同阶段的考虑
  - ​    上线前
  - ​    项目运行阶段
  - ​    线上出现OOM
-   4-两句话
  - ​    调优，从业务场景开始，没有业务场景的调优都是耍流氓！
  - ​    无监控，不调优！



#### 3-调优监控的依据

-   运行日志
-   异常堆栈
-   GC日志
-   线程快照
-   堆转储快照



#### 4-性能优化的步骤

第1步：熟悉业务场景

第2步（发现问题）：性能监控

- ​    GC 频繁
- ​    cpu load过高
- ​    OOM
- ​    内存泄漏
- ​    死锁
- ​    程序响应时间较长

第3步（排查问题）：性能分析

- ​    打印GC日志，通过GCviewer或者 http://gceasy.io来分析日志信息

- ​    灵活运用 命令行工具，jstack，jmap，jinfo等

- ​    dump出堆文件，使用内存分析工具分析文件

- ​    使用阿里Arthas，或jconsole，JVisualVM来实时查看JVM状态

- ​    jstack查看堆栈信息

  

第4步（解决问题）：性能调优

- ​    适当增加内存，根据业务背景选择垃圾回收器
- ​    优化代码，控制内存使用
- ​    增加机器，分散节点压力
- ​    合理设置线程池线程数量
- ​    使用中间件提高程序效率，比如缓存，消息队列等
- ​    其他.......

​    举例



#### 5-性能评价/测试指标

-   1-停顿时间（或响应时间）
-   2-吞吐量
-   3-并发数
-   4-内存占用
-   5-相互间的关系





### 2-OOM案例



#### 面试题

- 说到内存泄漏，问有没有碰到，内存泄漏怎么解决？（拼多多）

- 内存泄漏是怎么造成的？（拼多多、字节跳动）

- 如何理解内存泄漏问题？有哪些情况会导致内存泄露？如何解决？ (阿里)

  

#### OOM案例1：堆溢出 

- ##### 报错信息

  - java.lang.OutOfMemoryError: Java heap space

- ##### 案例模拟

  - 发送请求:http://localhost:8080/add

- ##### JVM参数配置

  - ```
    参数配置： 初始-Xms30M  -Xmx30M
    -XX:+PrintGCDetails -XX:MetaspaceSize=64m -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=heap/heapdump.hprof -XX:+PrintGCDateStamps 
    -Xms200M  -Xmx200M  -Xloggc:log/gc-oomHeap.log
    ```

- ##### 运行结果

- 	java.lang.OutOfMemoryError: Java heap space
  	  at java.util.Arrays.copyOf(Arrays.java:3210) ~[na:1.8.0_131]
  	  at java.util.Arrays.copyOf(Arrays.java:3181) ~[na:1.8.0_131]
  	  at java.util.ArrayList.grow(ArrayList.java:261) ~[na:1.8.0_131]
  	  at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:235) ~[na:1.8.0_131]
  	  at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:227) ~[na:1.8.0_131]
  - 运行程序得到 heapdump.hprof 文件。如下图所示：
  - ![](H:\笔记\JVM\img\图像 (2).bmp)

- ##### 原因及解决方案

  - 原因
    -  1、代码中可能存在大对象分配 
    -  2、可能存在内存泄漏，导致在多次GC之后，还是无法找到一块足够大的内存容纳当前对象。
  - 解决方法
  - 1、检查是否存在大对象的分配，最有可能的是大数组分配 
  - 2、通过jmap命令，把堆内存dump下来，使用MAT等工具分析一下，检查是否存在内存泄漏的问题
  - 3、如果没有找到明显的内存泄漏，使用 -Xmx 加大堆内存 
  - 4、还有一点容易被忽略，检查是否有大量的自定义的 Finalizable 对象，也有可能是框架内部提供的，考虑其存在的必要性

- #####  dump文件分析

  - jvisualvm分析

    - 接下来我们使用工具打开该文件，由于我们当前设置的内存比较小，所以该文件比较小，但是正常在线上环境，该文件是比较大的，通常是以G为单位。

    - jvisualvm工具分析堆内存文件heapdump.hprof：

      ![](H:\笔记\JVM\img\图像 (3).bmp)

    - ![](H:\笔记\JVM\img\图像 (4).bmp)

    - 通过jvisualvm工具查看，占用最多实例的类是哪个，这样就可以定位到我们的问题所在。

    - ![](H:\笔记\JVM\img\图像 (5).bmp)

  

- MAT分析

  -  使用MAT工具查看，能找到对应的线程及相应线程中对应实例的位置和代码：
  - ![](H:\笔记\JVM\img\图像 (6).bmp)

- #####  gc日志分析

  - ![](H:\笔记\JVM\img\图像.bmp)



#### OOM案例2：元空间溢出

- ##### 元空间存储数据类型

  - 方法区（Method Area）与 Java 堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、即时编译器编译后的代码等数据。虽然Java 虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做 Non-Heap（非堆），目的应该是与 Java 堆区分开来。
  - Java 虚拟机规范对方法区的限制非常宽松，除了和 Java 堆一样不需要连续的内存和可以选择固定大小或者可扩展外，还可以选择不实现垃圾收集。垃圾收集行为在这个区域是比较少出现的，其内存回收目标主要是针对常量池的回收和对类型的卸载。当方法区无法满足内存分配需求时，将抛出 OutOfMemoryError 异常。

- ##### 报错信息

  - ​      java.lang.OutOfMemoryError: Metaspace

- ##### 案例模拟

  - 示例代码

    - 

  - 发送请求

    -  http://localhost:8080/metaSpaceOom

  - 运行结果

    - ```
      我是加强类哦，输出print之前的加强方法
      我是print本人
      class com.atguiigu.jvmdemo.bean.People$$EnhancerByCGLIB$$6ef22046_10
      totalClass:934
      activeClass:934
      unloadedClass:0
      Caused by: java.lang.OutOfMemoryError: Metaspace
        at java.lang.ClassLoader.defineClass1(Native Method)
        at java.lang.ClassLoader.defineClass(ClassLoader.java:763)
        at sun.reflect.GeneratedMethodAccessor1.invoke(Unknown Source)
      ```

- ##### JVM参数配置

  - ```
    -XX:+PrintGCDetails -XX:MetaspaceSize=60m -XX:MaxMetaspaceSize=60m -Xss512K -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heap/heapdumpMeta.hprof  -XX:SurvivorRatio=8 -XX:+TraceClassLoading -XX:+TraceClassUnloading -XX:+PrintGCDateStamps  -Xms60M  -Xmx60M -Xloggc:log/gc-oomMeta.log
    ```

- ##### 原因及解决方案

  - JDK8后，元空间替换了永久代，元空间使用的是本地内存
  - 原因：
    - 运行期间生成了大量的代理类，导致方法区被撑爆，无法卸载
    - 应用长时间运行，没有重启
    - 元空间内存设置过小
  - 解决方法：
    - 因为该 OOM 原因比较简单，解决方法有如下几种：
      - 检查是否永久代空间或者元空间设置的过小
      - 检查代码中是否存在大量的反射操作
      - dump之后通过mat检查是否存在大量由于反射生成的代理类

- ##### 分析及解决

  - 查看监控

  - 查看GC状态

    - ![](H:\笔记\JVM\img\图像 (11).png)

    - 可以看到，FullGC 非常频繁，而且我们的方法区，占用了59190KB/1024 = 57.8M空间，几乎把整个方法区空间占用，所以得出的结论是方法区空间设置过小，或者存在大量由于反射生成的代理类。

    - EC 伊甸园区 容量

      EU 伊甸园区使用的容量

      OC 老年区的容量

      OU 老年区使用的容量

      MC 方法区

      CCSC 压缩类的容量

      CCSU 压缩类使用的容量

      YGC young GC的次数

      YGCT young GC的时间

      FGC full GC的次数

      FGCT full GC的时间

  - 查看GC日志

    - ```
      2021-04-21T00:04:09.052+0800: 109.779: [GC (Metadata GC Threshold) [PSYoungGen: 174K->32K(19968K)] 14926K->14784K(60928K), 0.0013218 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:09.054+0800: 109.780: [Full GC (Metadata GC Threshold) [PSYoungGen: 32K->0K(19968K)] [ParOldGen: 14752K->14752K(40960K)] 14784K->14752K(60928K), [Metaspace: 58691K->58691K(1103872K)], 0.0274454 secs] [Times: user=0.17 sys=0.00, real=0.03 secs] 
      2021-04-21T00:04:09.081+0800: 109.808: [GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] 14752K->14752K(60928K), 0.0009630 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:09.082+0800: 109.809: [Full GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] [ParOldGen: 14752K->14752K(40960K)] 14752K->14752K(60928K), [Metaspace: 58691K->58691K(1103872K)], 0.0301540 secs] [Times: user=0.17 sys=0.00, real=0.03 secs] 
      2021-04-21T00:04:22.476+0800: 123.202: [GC (Metadata GC Threshold) [PSYoungGen: 3683K->384K(19968K)] 18435K->15144K(60928K), 0.0015294 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:22.477+0800: 123.203: [Full GC (Metadata GC Threshold) [PSYoungGen: 384K->0K(19968K)] [ParOldGen: 14760K->14896K(40960K)] 15144K->14896K(60928K), [Metaspace: 58761K->58761K(1103872K)], 0.0299402 secs] [Times: user=0.16 sys=0.02, real=0.03 secs] 
      2021-04-21T00:04:22.508+0800: 123.233: [GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] 14896K->14896K(60928K), 0.0016583 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:22.509+0800: 123.235: [Full GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] [ParOldGen: 14896K->14751K(40960K)] 14896K->14751K(60928K), [Metaspace: 58761K->58692K(1103872K)], 0.0333369 secs] [Times: user=0.22 sys=0.02, real=0.03 secs] 
      2021-04-21T00:04:22.543+0800: 123.269: [GC (Metadata GC Threshold) [PSYoungGen: 229K->320K(19968K)] 14981K->15071K(60928K), 0.0014224 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:22.544+0800: 123.271: [Full GC (Metadata GC Threshold) [PSYoungGen: 320K->0K(19968K)] [ParOldGen: 14751K->14789K(40960K)] 15071K->14789K(60928K), [Metaspace: 58692K->58692K(1103872K)], 0.0498304 secs] [Times: user=0.42 sys=0.00, real=0.05 secs] 
      2021-04-21T00:04:22.594+0800: 123.321: [GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] 14789K->14789K(60928K), 0.0016910 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:22.596+0800: 123.322: [Full GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] [ParOldGen: 14789K->14773K(40960K)] 14789K->14773K(60928K), [Metaspace: 58692K->58692K(1103872K)], 0.0298989 secs] [Times: user=0.16 sys=0.02, real=0.03 secs] 
      2021-04-21T00:04:22.626+0800: 123.352: [GC (Metadata GC Threshold) [PSYoungGen: 0K->0K(19968K)] 14773K->14773K(60928K), 0.0013409 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:22.627+0800: 123.354: [Full GC (Metadata GC Threshold) [PSYoungGen: 0K->0K(19968K)] [ParOldGen: 14773K->14765K(40960K)] 14773K->14765K(60928K), [Metaspace: 58692K->58692K(1103872K)], 0.0298311 secs] [Times: user=0.17 sys=0.00, real=0.03 secs] 
      2021-04-21T00:04:22.657+0800: 123.384: [GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] 14765K->14765K(60928K), 0.0014417 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
      2021-04-21T00:04:22.659+0800: 123.385: [Full GC (Last ditch collection) [PSYoungGen: 0K->0K(19968K)] [ParOldGen: 14765K->14765K(40960K)] 14765K->14765K(60928K), [Metaspace: 58692K->58692K(1103872K)], 0.0253914 secs] [Times: user=0.30 sys=0.00, real=0.03 secs] 
      ```

    - 可以看到FullGC是由于方法区空间不足引起的，那么我们接下来分析到底是什么数据占用了大量的方法区。

    - ![](H:\笔记\JVM\img\图像 (12).png)

    - ![](H:\笔记\JVM\img\图像 (13).png)

  - 分析dump文件

    - jvisualvm分析
      - 导出dump文件，使用jvisualvm工具分析之：
      - ![](H:\笔记\JVM\img\图像 (14).png)
      - ![](H:\笔记\JVM\img\图像 (15).png)
      - ![](H:\笔记\JVM\img\图像 (16).png)
    -  MAT分析
      - 打开堆文件 heapdumpMeta.hprof：
      - 首先我们先确定是哪里的代码发生了问题，首先可以通过线程来确定，因为在实际生产环境中，有时候是无法确定是哪块代码引起的OOM，那么我们就需要先定位问题线程，然后定位代码，如下图所示。
      - ![](H:\笔记\JVM\img\图像 (17).png)
      - 定位到代码以后，发现有使用到cglib动态代理，那么我们猜想一下问题是不是由于产生了很多代理类，接下来，我们可以通过包看一下我们的类加载情况。
      - 这里发现Method类的实例非常多，查看with outging references
      - ![](H:\笔记\JVM\img\图像 (18).png)
      - 这里发现了很多的People类在调用相关的方法：
      - ![](H:\笔记\JVM\img\图像 (19).png)
      - 由于我们的代码是代理的People类，所以我们直接打开该类所在的包，打开如下图所示：
      - ![](H:\笔记\JVM\img\图像 (20).png)
      - 可以看到确实加载了很多的代理类。

  - 解决方案

    - 那么我们可以想一下解决方案，每次是不是可以只加载一个代理类即可，因为我们的需求其实是没有必要如此加载的，当然如果业务上确实需要加载很多类的话，那么我们就要考虑增大方法区大小了，所以我们这里修改代码如下：

      - ```
        enhancer.setUseCache(true);
        ```

    - enhancer.setUseCache(false)，选择为true的话，使用和更新一类具有相同属性生成的类的静态缓存，而不会在同一个类文件还继续被动态加载并视为不同的类，这个其实跟类的equals()和hashCode()有关，它们是与cglib内部的class cache的key相关的。再看程序运行结果如下：

      - ```
        我是加强类哦，输出print之前的加强方法
        我是print本人
        class com.atguiigu.jvmdemo.bean.People$$EnhancerByCGLIB$$6ef22046
        totalClass:6901
        activeClass:6901
        我是加强类哦，输出print之前的加强方法
        我是print本人
        class com.atguiigu.jvmdemo.bean.People$$EnhancerByCGLIB$$6ef22046
        totalClass:6901
        activeClass:6901+
        ```

    - 可以看到，几乎不变了，方法区也没有溢出。到此，问题基本解决，再就是把while循环去掉。

####   OOM案例3：GC overhead limit exceeded

- 案例模拟

  - 示例代码1

  - ```java
    /**
     * 案例3：测试 GC overhead limit exceeded
     * @author shkstart
     * @create 16:57
     */
    public class OOMTest {
        public static void main(String[] args) {
      test1();
    
    //  test2();
        }
    
        public static void test1() {
      int i = 0;
      List<String> list = new ArrayList<>();
      try {
          while (true) {
        list.add(UUID.randomUUID().toString().intern());
        i++;
    }
      } catch (Throwable e) {
          System.out.println("************i: " + i);
          e.printStackTrace();
          throw e;
      }
        }
    
        public static void test2() {
      String str = "";
      Integer i = 1;
      try {
          while (true) {
        i++;
        str += UUID.randomUUID();
    }
      } catch (Throwable e) {
          System.out.println("************i: " + i);
          e.printStackTrace();
          throw e;
      }
        }
    
    }
    ```

    - JVM配置

      - ```
        -XX:+PrintGCDetails  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heap/dumpExceeded.hprof -XX:+PrintGCDateStamps  -Xms10M  -Xmx10M -Xloggc:log/gc-oomExceeded.log
        ```

    - 报错信息

      - ```
        [Full GC (Ergonomics) [PSYoungGen: 2047K->2047K(2560K)] [ParOldGen: 7110K->7095K(7168K)] 9158K->9143K(9728K), [Metaspace: 3177K->3177K(1056768K)], 0.0479640 secs] [Times: user=0.23 sys=0.01, real=0.05 secs] 
        java.lang.OutOfMemoryError: GC overhead limit exceeded
        [Full GC (Ergonomics) [PSYoungGen: 2047K->2047K(2560K)] [ParOldGen: 7114K->7096K(7168K)] 9162K->9144K(9728K), [Metaspace: 3198K->3198K(1056768K)], 0.0408506 secs] [Times: user=0.22 sys=0.01, real=0.04 secs] 
        ```

        通过查看GC日志可以发现，系统在频繁性的做FULL GC，但是却没有回收掉多少空间，那么引起的原因可能是因为内存不足，也可能是存在内存泄漏的情况，接下来我们要根据堆dump文件来具体分析。

  - 示例代码2

    - ```java
          public static void test2() {
        String str = "";
        Integer i = 1;
        try {
            while (true) {
          i++;
          str += UUID.randomUUID();
      }
        } catch (Throwable e) {
            System.out.println("************i: " + i);
            e.printStackTrace();
            throw e;
        }
          }
      ```

    -  JVM配置

      - ```
        -XX:+PrintGCDetails  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heap/dumpHeap1.hprof -XX:+PrintGCDateStamps  -Xms10M  -Xmx10M -Xloggc:log/gc-oomHeap1.log
        ```

  - 代码解析

    - 你可能会疑惑，看似demo也没有差太多，为什么第二个没有报GC overhead limit exceeded呢？以上两个demo的区别在于：

      - Java heap space的demo每次都能回收大部分的对象（中间产生的UUID），只不过有一个对象是无法回收的，慢慢长大，直到内存溢出
      - GC overhead limit exceeded的demo由于每个字符串都在被list引用，所以无法回收，很快就用完内存，触发不断回收的机制。

    - 报错信息：

      - ```
        [Full GC (Ergonomics) [PSYoungGen: 2047K->2047K(2560K)] [ParOldGen: 7110K->7095K(7168K)] 9158K->9143K(9728K), [Metaspace: 3177K->3177K(1056768K)], 0.0479640 secs] [Times: user=0.23 sys=0.01, real=0.05 secs] 
        java.lang.OutOfMemoryError: GC overhead limit exceeded
        [Full GC (Ergonomics) [PSYoungGen: 2047K->2047K(2560K)] [ParOldGen: 7114K->7096K(7168K)] 9162K->9144K(9728K), [Metaspace: 3198K->3198K(1056768K)], 0.0408506 secs] [Times: user=0.22 sys=0.01, real=0.04 secs] 
        ```

    - 通过查看GC日志可以发现，系统在频繁性的做FULL GC，但是却没有回收掉多少空间，那么引起的原因可能是因为内存不足，也可能是存在内存泄漏的情况，接下来我们要根据堆DUMP文件来具体分析。

  - 分析及解决

    - 第1步：定位问题代码块
      - jvisualvm分析
        - 这里就定位到了具体的线程中具体出现问题的代码的位置，进而进行优化即可。
        - ![](H:\笔记\JVM\img\图像 (1).png)
        - ![](H:\笔记\JVM\img\图像 (2).png)
      - MAT分析
      - ![](H:\笔记\JVM\img\图像 (3).png)
      - ![](H:\笔记\JVM\img\图像 (4).png)
      - ![](H:\笔记\JVM\img\图像 (5).png)
      - 通过线程分析如下图所示，可以定位到发生OOM的代码块
      - ![](H:\笔记\JVM\img\图像 (6).png)
      - ![](H:\笔记\JVM\img\图像 (7).png)
    - 第2步：分析dump文件直方图
      - 看到发生OOM是因为进行了死循环，不停的往 ArrayList 存放字符串常量，JDK1.8以后，字符串常量池移到了堆中存储，所以最终导致内存不足发生了OOM。
      - 打开Histogram，可以看到，String类型的字符串占用了大概8M的空间，几乎把堆占满，但是还没有占满，所以这也符合Sun 官方对此的定义：超过98%的时间用来做GC并且回收了不到2%的堆内存时会抛出此异常，本质是一个预判性的异常，抛出该异常时系统没有真正的内存溢出。
      - ![](H:\笔记\JVM\img\图像 (8).png)
      - ![](H:\笔记\JVM\img\图像 (9).png)
      - ![](H:\笔记\JVM\img\图像 (10).png)
    - 第3步：代码修改
      - 根据业务来修改是否需要死循环。
      - 原因：
        - 这个是JDK6新加的错误类型，一般都是堆太小导致的。Sun 官方对此的定义：超过98%的时间用来做GC并且回收了不到2%的堆内存时会抛出此异常。本质是一个预判性的异常，抛出该异常时系统没有真正的内存溢出
      - 解决方法：
        - 检查项目中是否有大量的死循环或有使用大内存的代码，优化代码。
        - 添加参数 `-XX:-UseGCOverheadLimit` 禁用这个检查，其实这个参数解决不了内存问题，只是把错误的信息延后，最终出现 java.lang.OutOfMemoryError: Java heap space。
        - dump内存，检查是否存在内存泄漏，如果没有，加大内存。

####   OOM案例4：线程溢出

- 报错信息

  - java.lang.OutOfMemoryError : unable to create new native Thread

- 问题原因

  - 出现这种异常，基本上都是创建了大量的线程导致的

- 案例模拟

  - 说明

    - 操作系统会崩溃，linux无法再进行任何命令，mac/windows可能直接关机重启。鉴于以上原因，我们在虚拟机进行测试。

  - 示例代码

    - ```java
      /**
       * 测试4：线程溢出
       * @author shkstart
       * @create 17:45
       */
      public class TestNativeOutOfMemoryError {
          public static void main(String[] args) {
              for (int i = 0; ; i++) {
                  System.out.println("i = " + i);
                  new Thread(new HoldThread()).start();
              }
          }
      }
      
      class HoldThread extends Thread {
          CountDownLatch cdl = new CountDownLatch(1);
      
          @Override
          public void run() {
              try {
                  cdl.await();
              } catch (InterruptedException e) {
              }
          }
      }
      
      ```

  - 运行结果

    - ```
      i = 15241
      Exception in thread "main" java.lang.OutOfMemoryError: unable to create new native thread
              at java.lang.Thread.start0(Native Method)
              at java.lang.Thread.start(Thread.java:717)
              at TestNativeOutOfMemoryError.main(TestNativeOutOfMemoryError.java:9)
      ```

    - ![](H:\笔记\JVM\img\图像 (21).png)

- 分析及解决

  - 解决方向1
    - 通过 -Xss 设置每个线程栈大小的容量
    - JDK5.0以后每个线程堆栈大小为1M,以前每个线程堆栈大小为256K。
    - 正常情况下，在相同物理内存下，减小这个值能生成更多的线程。但是操作系统对一个进程内的线程数还是有限制的,不能无限生成,经验值在3000~5000左右。
    - 能创建的线程数的具体计算公式如下：
      - (MaxProcessMemory - JVMMemory - ReservedOsMemory) / (ThreadStackSize) = Number of threads
      - MaxProcessMemory 指的是进程可寻址的最大空间
      - JVMMemory     JVM内存
      - ReservedOsMemory 保留的操作系统内存
      - ThreadStackSize   线程栈的大小
    - 在Java语言里， 当你创建一个线程的时候，虚拟机会在JVM内存创建一个Thread对象同时创建一个操作系统线程，而这个系统线程的内存用的不是JVMMemory，而是系统中剩下的内存(MaxProcessMemory - JVMMemory - ReservedOsMemory)。
    - 由公式得出结论：你给JVM内存越多，那么你能创建的线程越少，越容易发生java.lang.OutOfMemoryError: unable to create new native thread
  - 解决方向2
    - 线程总数也受到系统空闲内存和操作系统的限制，检查是否该系统下有此限制：
      - /proc/sys/kernel/pid_max          系统最大pid值，在大型系统里可适当调大
      - /proc/sys/kernel/threads-max     系统允许的最大线程数
      - maxuserprocess（ulimit -u）   系统限制某用户下最多可以运行多少进程或线程
      - /proc/sys/vm/max_map_count     