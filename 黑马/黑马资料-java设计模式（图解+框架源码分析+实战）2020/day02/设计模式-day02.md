# 4，创建型模式

## 4.2 工厂模式

### 4.2.1 概述

需求：设计一个咖啡店点餐系统。  

设计一个咖啡类（Coffee），并定义其两个子类（美式咖啡【AmericanCoffee】和拿铁咖啡【LatteCoffee】）；再设计一个咖啡店类（CoffeeStore），咖啡店具有点咖啡的功能。

具体类的设计如下：

<img src="img/工厂设计模式引入.png" style="zoom:80%;" />

**在java中，万物皆对象，这些对象都需要创建，如果创建的时候直接new该对象，就会对该对象耦合严重，假如我们要更换对象，所有new对象的地方都需要修改一遍，这显然违背了软件设计的开闭原则。**如果我们使用工厂来生产对象，我们就只和工厂打交道就可以了，彻底和对象解耦，如果要更换对象，直接在工厂里更换该对象即可，达到了与对象解耦的目的；所以说，工厂模式最大的优点就是：**解耦**。

在本教程中会介绍三种工厂的使用

* 简单工厂模式（不属于GOF的23种经典设计模式）
* 工厂方法模式
* 抽象工厂模式



工厂模式（Factory Pattern）提供了一种创建对象的最佳方式。我们不必关心对象的创建细节，只需要根据不同情况获取不同产品即可。难点：写好我们的工厂

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片3.png)

**原始代码的咖啡店和咖啡耦合严重**

```java
/**
 * @version v1.0
 * @ClassName: Coffee
 * @Description: 咖啡类
 * @Author: 黑马程序员
 */
public abstract class Coffee {

    public abstract String getName();

    //加糖
    public void addsugar() {
        System.out.println("加糖");
    }

    //加奶
    public void addMilk() {
        System.out.println("加奶");
    }
}

```

LatteCoffee

```java
/**
 * @version v1.0
 * @ClassName: LatteCoffee
 * @Description: 拿铁咖啡
 * @Author: 黑马程序员
 */
public class LatteCoffee extends Coffee {

    public String getName() {
        return "拿铁咖啡";
    }
}

```

AmericanCoffee

```java
/**
 * @version v1.0
 * @ClassName: AmericanCoffee
 * @Description: 没事咖啡
 * @Author: 黑马程序员
 */
public class AmericanCoffee extends Coffee {

    public String getName() {
        return "美式咖啡";
    }
}

```

CoffeeStore

```java
/**
 * @version v1.0
 * @ClassName: CoffeeStore
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class CoffeeStore {

    public Coffee orderCoffee(String type) {
        //声明Coffee类型的变量，根据不同类型创建不同的coffee子类对象
        Coffee coffee = null;
        if("american".equals(type)) {
            coffee = new AmericanCoffee();
        } else if("latte".equals(type)) {
            coffee = new LatteCoffee();
        } else {
            throw new RuntimeException("对不起，您所点的咖啡没有");
        }
        //加配料
        coffee.addMilk();
        coffee.addsugar();

        return coffee;
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
        //1,创建咖啡店类
        CoffeeStore store = new CoffeeStore();
        //2,点咖啡
        Coffee coffee = store.orderCoffee("american");

        System.out.println(coffee.getName());
    }
}
```



### 4.2.2 简单工厂模式

简单工厂不是一种设计模式，反而比较像是一种编程习惯。

#### 4.2.2.1 结构

简单工厂包含如下角色：

* 抽象产品 ：定义了产品的规范，描述了产品的主要特性和功能。
* 具体产品 ：实现或者继承抽象产品的子类
* 具体工厂 ：提供了创建产品的方法，调用者通过该方法来获取产品。

#### 4.2.2.2 实现

现在使用简单工厂对上面案例进行改进，类图如下：

***咖啡店和咖啡解耦，咖啡店依赖于咖啡工厂***

<img src="img/简单工厂模式.png" style="zoom:70%;" />

工厂类代码如下：

```java
/**
 * @version v1.0
 * @ClassName: SimpleCoffeeFactory
 * @Description: 简单咖啡工厂类，用来生产咖啡
 * @Author: 黑马程序员
 */
public class SimpleCoffeeFactory {

    public Coffee createCoffee(String type) {
        //声明Coffee类型的变量，根据不同类型创建不同的coffee子类对象
        Coffee coffee = null;
        if("american".equals(type)) {
            coffee = new AmericanCoffee();
        } else if("latte".equals(type)) {
            coffee = new LatteCoffee();
        } else {
            throw new RuntimeException("对不起，您所点的咖啡没有");
        }

        return coffee;
    }
}

```

工厂（factory）处理创建对象的细节，一旦有了SimpleCoffeeFactory，CoffeeStore类中的orderCoffee()就变成此对象的客户，后期如果需要Coffee对象直接从工厂中获取即可。这样也就解除了和Coffee实现类的耦合，同时又产生了新的耦合，CoffeeStore对象和SimpleCoffeeFactory工厂对象的耦合，工厂对象和商品对象的耦合。

后期如果再加新品种的咖啡，我们势必要需求修改SimpleCoffeeFactory的代码，违反了开闭原则。工厂类的客户端可能有很多，比如创建美团外卖等，这样只需要修改工厂类的代码，省去其他的修改操作。

#### 4.2.2.4 优缺点

**优点：**

封装了创建对象的过程，可以通过参数直接获取对象。把对象的创建和业务逻辑层分开，这样以后就避免了修改客户代码，如果要实现新产品直接修改工厂类，而不需要在原代码中修改，这样就降低了客户代码修改的可能性，更加容易扩展。

**缺点：**

增加新产品时还是需要修改工厂类的代码，违背了“开闭原则”。

#### 4.2.2.3 扩展

**静态工厂**

在开发中也有一部分人将工厂类中的创建对象的功能定义为静态的，这个就是静态工厂模式，它也不是23种设计模式中的。代码如下：

```java
public class SimpleCoffeeFactory {

    public static Coffee createCoffee(String type) {
        Coffee coffee = null;
        if("americano".equals(type)) {
            coffee = new AmericanoCoffee();
        } else if("latte".equals(type)) {
            coffee = new LatteCoffee();
        }
        return coffe;
    }
}
```

尚硅谷的案例：

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片4.png)

三个角色
Factory：工厂角色， WuLinFactory
Product：抽象产品角色，Car
ConcreteProduct：具体产品角色， VanCar、MiniCar

AbstractCar

```java
/*
* TODO 工厂的产品
*  如果把一个功能 提升一个层次 定义抽象（抽象类 接口）
*  多实现 就会有多功能
* */
public abstract class AbstractCar {
    String engine;

    public abstract void run();

}

```

CarFactory

```java
/*
 * TODO 简单工厂
 *  1.产品数量有限
 * */
public class CarFactory {

    public AbstractCar newCar(String type) {
        if ("van".equals(type)) {
            return new VanCar();
        } else if ("mini".equals(type)) {
            return new MiniCar();
        }
        /*
        * TODO 如果想要更多的产品 就会违反开闭原则
        *  应该 对修改关闭 对扩展开放 =》 应该扩展出一个类来造车
        * */
        return null;
    }
}

```

MiniCar

```java
public class MiniCar extends AbstractCar {
    public MiniCar() {
        this.engine = "四缸水平对置发动机";
    }

    @Override
    public void run() {
        System.out.println(engine + "-->嘟嘟嘟。。。");
    }
}

```

VanCar

```java
/*
 * TODO 具体的产品
 * */
public class VanCar extends AbstractCar {
    public VanCar() {
        this.engine = "单缸柴油机";
    }

    @Override
    public void run() {
        System.out.println(engine + "-->哒哒哒。。。。");
    }
}

```

MainTest

```java
public class MainTest {
    public static void main(String[] args) {
        CarFactory carFactory = new CarFactory();
        AbstractCar van = carFactory.newCar("van");
        System.out.println(van);
        AbstractCar mini = carFactory.newCar("mini");
        System.out.println(mini);
    }
}

```



### 4.2.3 工厂方法模式

**针对上例中的缺点，使用工厂方法模式就可以完美的解决，完全遵循开闭原则。**

#### 4.2.3.1 概念

定义一个用于创建对象的接口，让子类决定实例化哪个产品类对象。工厂方法使一个产品类的实例化延迟到其工厂的子类。

#### 4.2.3.2 结构

工厂方法模式的主要角色：

* 抽象工厂（Abstract Factory）：提供了创建产品的接口，调用者通过它访问具体工厂的工厂方法来创建产品。
* 具体工厂（ConcreteFactory）：主要是实现抽象工厂中的抽象方法，完成具体产品的创建。
* 抽象产品（Product）：定义了产品的规范，描述了产品的主要特性和功能。
* 具体产品（ConcreteProduct）：实现了抽象产品角色所定义的接口，由具体工厂来创建，它同具体工厂之间一一对应。

#### 4.2.3.3 实现

使用工厂方法模式对上例进行改进，类图如下：

<img src="img/工厂方法模式.png" style="zoom:70%;" />

代码如下：

抽象工厂：

```java
/**
 * @version v1.0
 * @ClassName: CoffeeFactory
 * @Description: CoffeeFactory ： 抽象工厂
 * @Author: 黑马程序员
 */
public interface CoffeeFactory {

    //创建咖啡对象的方法
    Coffee createCoffee();
}

```

具体工厂：

```java
/**
 * @version v1.0
 * @ClassName: LatteCoffeeFactory
 * @Description: 拿铁咖啡工厂，专门用来生产拿铁咖啡
 * @Author: 黑马程序员
 */
public class LatteCoffeeFactory implements CoffeeFactory {

    public Coffee createCoffee() {
        return new LatteCoffee();
    }
}

```

AmericanCoffeeFactory

```java
/**
 * @version v1.0
 * @ClassName: AmericanCoffeeFactory
 * @Description: 美式咖啡工厂对象，专门用来生产美式咖啡
 * @Author: 黑马程序员
 */
public class AmericanCoffeeFactory implements CoffeeFactory {

    public Coffee createCoffee() {
        return new AmericanCoffee();
    }
}
```

咖啡店类：

```java
/**
 * @version v1.0
 * @ClassName: CoffeeStore
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class CoffeeStore {

    private CoffeeFactory factory;

    public void setFactory(CoffeeFactory factory) {
        this.factory = factory;
    }

    //点咖啡功能
    public Coffee orderCoffee() {
        Coffee coffee = factory.createCoffee();
        //加配料
        coffee.addMilk();
        coffee.addsugar();
        return coffee;
    }
}

```

Coffee

```java
/**
 * @version v1.0
 * @ClassName: Coffee
 * @Description: 咖啡类
 * @Author: 黑马程序员
 */
public abstract class Coffee {

    public abstract String getName();

    //加糖
    public void addsugar() {
        System.out.println("加糖");
    }

    //加奶
    public void addMilk() {
        System.out.println("加奶");
    }
}
```

AmericanCoffee

```java
/**
 * @version v1.0
 * @ClassName: AmericanCoffee
 * @Description: 没事咖啡
 * @Author: 黑马程序员
 */
public class AmericanCoffee extends Coffee {

    public String getName() {
        return "美式咖啡";
    }
}
```

LatteCoffee

```java
/**
 * @version v1.0
 * @ClassName: LatteCoffee
 * @Description: 拿铁咖啡
 * @Author: 黑马程序员
 */
public class LatteCoffee extends Coffee {

    public String getName() {
        return "拿铁咖啡";
    }
}
```

从以上的编写的代码可以看到，要增加产品类时也要相应地增加工厂类，不需要修改工厂类的代码了，这样就解决了简单工厂模式的缺点。

工厂方法模式是简单工厂模式的进一步抽象。由于使用了多态性，工厂方法模式保持了简单工厂模式的优点，而且克服了它的缺点。

#### 4.2.3.4 优缺点

**优点：**

- 用户只需要知道具体工厂的名称就可得到所要的产品，无须知道产品的具体创建过程；
- 在系统增加新的产品时只需要添加具体产品类和对应的具体工厂类，无须对原工厂进行任何修改，满足开闭原则；

**缺点：**

* 每增加一个产品就要增加一个具体产品类和一个对应的具体工厂类，这增加了系统的复杂度。



尚硅谷案例：

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片5.png)

四个角色
Product：抽象产品
ConcreteProduct：具体产品
Factory：抽象工厂
ConcreteFactory：具体工厂

```java
/*
 * TODO 工厂的产品
 *  如果把一个功能 提升一个层次 定义抽象（抽象类 接口）
 *  多实现 就会有多功能
 * */
public abstract class AbstractCar {
    String engine;

    public abstract void run();

}
```

AbstactCarFactory

```java
/*
 * TODO 抽象出 工厂的层级
 * */
public abstract class AbstactCarFactory {
    public abstract AbstractCar newCar();
}
```

MiniCar

```java
public class MiniCar extends AbstractCar {
    public MiniCar() {
        this.engine = "四缸水平对置发动机";
    }

    @Override
    public void run() {
        System.out.println(engine + "-->嘟嘟嘟。。。");
    }
}
```

MiniCarFactory

```java
public class MiniCarFactory extends AbstactCarFactory {
    @Override
    public AbstractCar newCar() {
        return new MiniCar();
    }
}
```

RaceCar

```java
public class RaceCar extends AbstractCar{
    public RaceCar() {
        this.engine = "八缸水平发动机";
    }
    @Override
    public void run() {
        System.out.println(engine + "-->咻。。。");
    }
}
```

RaceCarFactory

```java
//TODO 该工厂只造RaceCar
public class RaceCarFactory extends AbstactCarFactory {
    @Override
    public AbstractCar newCar() {
        return new RaceCar();
    }
}
```

VanCar

```java
/*
 * TODO 具体的产品
 * */
public class VanCar extends AbstractCar {
    public VanCar() {
        this.engine = "单缸柴油机";
    }

    @Override
    public void run() {
        System.out.println(engine + "-->哒哒哒。。。。");
    }
}
```

VanCarFactory

```java
//TODO 该工厂只造van
public class VanCarFactory extends AbstactCarFactory {
    @Override
    public AbstractCar newCar() {
        return new VanCar();
    }
}
```

MainTest

```java
public class MainTest {
    public static void main(String[] args) {
        VanCarFactory vanCarFactory = new VanCarFactory();
        AbstractCar van = vanCarFactory.newCar();
        System.out.println(van);
        van.run();
        MiniCarFactory miniCarFactory = new MiniCarFactory();
        AbstractCar mini = miniCarFactory.newCar();
        System.out.println(mini);
        mini.run();
        RaceCarFactory raceCarFactory = new RaceCarFactory();
        AbstractCar raceCar = raceCarFactory.newCar();
        System.out.println(raceCar);
        raceCar.run();
    }
}
```

### 4.2.4 抽象工厂模式

前面介绍的工厂方法模式中考虑的是一类产品的生产，如畜牧场只养动物、电视机厂只生产电视机、传智播客只培养计算机软件专业的学生等。

这些工厂只生产同种类产品，同种类产品称为同等级产品，也就是说：工厂方法模式只考虑生产同等级的产品，但是在现实生活中许多工厂是综合型的工厂，能生产多等级（种类） 的产品，如电器厂既生产电视机又生产洗衣机或空调，大学既有软件专业又有生物专业等。

本节要介绍的抽象工厂模式将考虑多等级产品的生产，将同一个具体工厂所生产的位于不同等级的一组产品称为一个产品族，**下图所示横轴是产品等级，也就是同一类产品；纵轴是产品族，也就是同一品牌的产品，同一品牌的产品产自同一个工厂。**

<img src="img/image-20200401214509176.png" style="zoom:67%;" />

<img src="img/image-20200401222951963.png" style="zoom:67%;" />

#### 4.2.4.1 概念

是一种为访问类提供一个创建一组相关或相互依赖对象的接口，且访问类无须指定所要产品的具体类就能得到同族的不同等级的产品的模式结构。

抽象工厂模式是工厂方法模式的升级版本，工厂方法模式只生产一个等级的产品，而抽象工厂模式可生产多个等级的产品。

#### 4.2.4.2 结构

抽象工厂模式的主要角色如下：

* 抽象工厂（Abstract Factory）：提供了创建产品的接口，它包含多个创建产品的方法，可以创建多个不同等级的产品。
* 具体工厂（Concrete Factory）：主要是实现抽象工厂中的多个抽象方法，完成具体产品的创建。
* 抽象产品（Product）：定义了产品的规范，描述了产品的主要特性和功能，抽象工厂模式有多个抽象产品。
* 具体产品（ConcreteProduct）：实现了抽象产品角色所定义的接口，由具体工厂来创建，它 同具体工厂之间是多对一的关系。

#### 4.2.4.2 实现

现咖啡店业务发生改变，不仅要生产咖啡还要生产甜点，如提拉米苏、抹茶慕斯等，要是按照工厂方法模式，需要定义提拉米苏类、抹茶慕斯类、提拉米苏工厂、抹茶慕斯工厂、甜点工厂类，很容易发生类爆炸情况。其中拿铁咖啡、美式咖啡是一个产品等级，都是咖啡；提拉米苏、抹茶慕斯也是一个产品等级；拿铁咖啡和提拉米苏是同一产品族（也就是都属于意大利风味），美式咖啡和抹茶慕斯是同一产品族（也就是都属于美式风味）。所以这个案例可以使用抽象工厂模式实现。类图如下：

<img src="img/抽象工厂模式.png" style="zoom:67%;" />

代码如下：

Dessert

```java
/**
 * @version v1.0
 * @ClassName: Dessert
 * @Description: 甜品抽象类
 * @Author: 黑马程序员
 */
public abstract class Dessert {

    public abstract void show();
}
```

 Coffee 

```java
/**
 * @version v1.0
 * @ClassName: Coffee
 * @Description: 咖啡类
 * @Author: 黑马程序员
 */
public abstract class Coffee {

    public abstract String getName();

    //加糖
    public void addsugar() {
        System.out.println("加糖");
    }

    //加奶
    public void addMilk() {
        System.out.println("加奶");
    }
}
```

LatteCoffee

```java
/**
 * @version v1.0
 * @ClassName: LatteCoffee
 * @Description: 拿铁咖啡
 * @Author: 黑马程序员
 */
public class LatteCoffee extends Coffee {

    public String getName() {
        return "拿铁咖啡";
    }
}
```

AmericanCoffee 

```java
/**
 * @version v1.0
 * @ClassName: AmericanCoffee
 * @Description: 没事咖啡
 * @Author: 黑马程序员
 */
public class AmericanCoffee extends Coffee {

    public String getName() {
        return "美式咖啡";
    }
}
```

MatchaMousse 

```java
/**
 * @version v1.0
 * @ClassName: MatchaMousse
 * @Description: 抹茶慕斯类
 * @Author: 黑马程序员
 */
public class MatchaMousse extends Dessert {
    public void show() {
        System.out.println("抹茶慕斯");
    }
}
```

Trimisu

```java
/**
 * @version v1.0
 * @ClassName: Trimisu
 * @Description: 提拉米苏类
 * @Author: 黑马程序员
 */
public class Trimisu extends Dessert {
    public void show() {
        System.out.println("提拉米苏");
    }
}
```

抽象工厂：

```java
/**
 * @version v1.0
 * @ClassName: DessertFactory
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public interface DessertFactory {

    //生产咖啡的功能
    Coffee createCoffee();

    //生产甜品的功能
    Dessert createDessert();
}

```

具体工厂：

```java
/**
 * @version v1.0
 * @ClassName: AmericanDessertFactory
 * @Description:
 *         美式风味的甜品工厂
 *             生产美式咖啡和抹茶慕斯
 * @Author: 黑马程序员
 */
public class AmericanDessertFactory implements DessertFactory {

    public Coffee createCoffee() {
        return new AmericanCoffee();
    }

    public Dessert createDessert() {
        return new MatchaMousse();
    }
}

```

 ItalyDessertFactory 

```java
/**
 * @version v1.0
 * @ClassName: ItalyDessertFactory
 * @Description:
 *
 *      意大利风味甜品工厂
 *          生产拿铁咖啡和提拉米苏甜品
 * @Author: 黑马程序员
 */
public class ItalyDessertFactory implements DessertFactory {

    public Coffee createCoffee() {
        return new LatteCoffee();
    }

    public Dessert createDessert() {
        return new Trimisu();
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
        //创建的是意大利风味甜品工厂对象
        //ItalyDessertFactory factory = new ItalyDessertFactory();
        AmericanDessertFactory factory = new AmericanDessertFactory();
        //获取拿铁咖啡和提拉米苏甜品
        Coffee coffee = factory.createCoffee();
        Dessert dessert = factory.createDessert();

        System.out.println(coffee.getName());
        dessert.show();
    }
}
```

如果要加同一个产品族的话，只需要再加一个对应的工厂类即可，不需要修改其他的类。

#### 4.2.4.3 优缺点

**优点：**

当一个产品族中的多个对象被设计成一起工作时，它能保证客户端始终只使用同一个产品族中的对象。

**缺点：**

当产品族中需要增加一个新的产品时，所有的工厂类都需要进行修改。

#### 4.2.4.4 使用场景

* 当需要创建的对象是一系列相互关联或相互依赖的产品族时，如电器工厂中的电视机、洗衣机、空调等。

* 系统中有多个产品族，但每次只使用其中的某一族产品。如有人只喜欢穿某一个品牌的衣服和鞋。

* 系统中提供了产品的类库，且所有产品的接口相同，客户端不依赖产品实例的创建细节和内部结构。

如：输入法换皮肤，一整套一起换。生成不同操作系统的程序。



尚硅谷案例

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片7.png)

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片6.png)

```java
/*
 * TODO 工厂的产品
 *  如果把一个功能 提升一个层次 定义抽象（抽象类 接口）
 *  多实现 就会有多功能
 * */
public abstract class AbstractCar {
    String engine;

    public abstract void run();
}
```

AbstractMask 

```java
public abstract class AbstractMask {
    public Integer price;

    public abstract void protectMe();
}
```

CommonMask

```java
public class CommonMask extends AbstractMask {

    public CommonMask() {
        this.price = 1;
    }

    @Override
    public void protectMe() {
        System.out.println("普通口罩。。。简单防护");
    }
}
```

N95Mask

```java
public class N95Mask extends AbstractMask{

    public N95Mask() {
        this.price = 2;
    }

    @Override
    public void protectMe() {
        System.out.println("超强防护");
    }
}
```

MiniCar

```java
public class MiniCar extends AbstractCar {
    public MiniCar() {
        this.engine = "四缸水平对置发动机";
    }

    @Override
    public void run() {
        System.out.println(engine + "-->嘟嘟嘟。。。");
    }
}
```

VanCar 

```java
/*
 * TODO 具体的产品
 * */
public class VanCar extends AbstractCar {
    public VanCar() {
        this.engine = "单缸柴油机";
    }

    @Override
    public void run() {
        System.out.println(engine + "-->哒哒哒。。。。");
    }
}
```

WuLinFactory

```java
/*
* TODO 最顶层的规范 可以理解为 总厂
* */
public abstract class WuLinFactory {

    abstract AbstractCar abstractCar();

    abstract AbstractMask abstractMask();
}
```

WuLinMaskFactory

```java
public abstract class WuLinMaskFactory extends WuLinFactory {
    AbstractCar abstractCar(){
        return null;
    }

    abstract AbstractMask abstractMask();
}
```

RaceCar

```java
public class RaceCar extends AbstractCar {
    public RaceCar() {
        this.engine = "八缸水平发动机";
    }
    @Override
    public void run() {
        System.out.println(engine + "-->咻。。。");
    }
}
```

WuLinCarFactory

```java
public abstract class WuLinCarFactory extends WuLinFactory {

    abstract AbstractCar abstractCar();

    @Override
    AbstractMask abstractMask() {
        return null;
    }
}
```

WuLinN95MaskFactory

```java
public class WuLinN95MaskFactory extends WuLinMaskFactory{
    @Override
    AbstractCar abstractCar() {
        return null;
    }

    @Override
    AbstractMask abstractMask() {
        return new N95Mask();
    }
}
```

WuLinCommonMaskFactory

```java
public class WuLinCommonMaskFactory extends WuLinMaskFactory {
    @Override
    AbstractCar abstractCar() {
        return null;
    }

    @Override
    AbstractMask abstractMask() {
        return new CommonMask();
    }
}
```

WuLinRaceCarFactory

```java
public class WuLinRaceCarFactory extends WuLinCarFactory{
    @Override
    AbstractCar abstractCar() {
        return new RaceCar();
    }

    @Override
    AbstractMask abstractMask() {
        return null;
    }
}
```

WuLinVanCarFactory

```java
//TODO 具体的工厂 造车
public class WuLinVanCarFactory extends WuLinCarFactory{
    @Override
    AbstractCar abstractCar() {
        return new VanCar();
    }

    @Override
    AbstractMask abstractMask() {
        return null;
    }
}
```



### 4.2.5 模式扩展

**简单工厂+配置文件解除耦合**

可以通过工厂模式+配置文件的方式解除工厂对象和产品对象的耦合。在工厂类中加载配置文件中的全类名，并创建对象进行存储，客户端如果需要对象，直接进行获取即可。

第一步：定义配置文件

为了演示方便，我们使用properties文件作为配置文件，名称为bean.properties

```properties
american=com.itheima.pattern.factory.config_factory.AmericanCoffee
latte=com.itheima.pattern.factory.config_factory.LatteCoffee
```

Coffee 

```java
/**
 * @version v1.0
 * @ClassName: Coffee
 * @Description: 咖啡类
 * @Author: 黑马程序员
 */
public abstract class Coffee {

    public abstract String getName();

    //加糖
    public void addsugar() {
        System.out.println("加糖");
    }

    //加奶
    public void addMilk() {
        System.out.println("加奶");
    }
}
```

AmericanCoffee

```java
/**
 * @version v1.0
 * @ClassName: AmericanCoffee
 * @Description: 没事咖啡
 * @Author: 黑马程序员
 */
public class AmericanCoffee extends Coffee {

    public String getName() {
        return "美式咖啡";
    }
}
```

 LatteCoffee

```java
/**
 * @version v1.0
 * @ClassName: LatteCoffee
 * @Description: 拿铁咖啡
 * @Author: 黑马程序员
 */
public class LatteCoffee extends Coffee {

    public String getName() {
        return "拿铁咖啡";
    }
}
```

第二步：改进工厂类

```java
/**
 * @version v1.0
 * @ClassName: CoffeeFactory
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class CoffeeFactory {

    //加载配置文件，获取配置文件中配置的全类名，并创建该类的对象进行存储
    //1,定义容器对象存储咖啡对象
    private static HashMap<String,Coffee> map = new HashMap<String, Coffee>();

    //2,加载配置文件， 只需要加载一次 =》静态代码块
    static {
        //2.1 创建Properties对象
        Properties p = new Properties();
        //2.2 调用p对象中的load方法进行配置文件的加载
        InputStream is = CoffeeFactory.class.getClassLoader().getResourceAsStream("bean.properties");
        try {
            p.load(is);
            //从p集合中获取全类名并创建对象
            Set<Object> keys = p.keySet();
            for (Object key : keys) {
                String className = p.getProperty((String) key);
                //通过反射技术创建对象
                Class clazz = Class.forName(className);
                Coffee coffee = (Coffee) clazz.newInstance();
                //将名称和对象存储到容器中
                map.put((String)key,coffee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //根据名称获取对象
    public static Coffee createCoffee(String name) {
        return map.get(name);
    }
}

```

静态成员变量用来存储创建的对象（键存储的是名称，值存储的是对应的对象），而读取配置文件以及创建对象写在静态代码块中，目的就是只需要执行一次。



### 4.2.6 JDK源码解析-Collection.iterator方法

```java
public class Demo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("令狐冲");
        list.add("风清扬");
        list.add("任我行");

        //获取迭代器对象
        Iterator<String> it = list.iterator();
        //使用迭代器遍历
        while(it.hasNext()) {
            String ele = it.next();
            System.out.println(ele);
        }
    }
}
```

对上面的代码大家应该很熟，使用迭代器遍历集合，获取集合中的元素。而单列集合获取迭代器的方法就使用到了工厂方法模式。我们看通过类图看看结构：

<img src="img/JDK源码解析.png" style="zoom:75%;" />

**Collection接口是抽象工厂类，ArrayList是具体的工厂类；Iterator接口是抽象商品类，ArrayList类中的Iter内部类是具体的商品类。在具体的工厂类中iterator()方法创建具体的商品类的对象。**

> 另：
>
> ​	1,DateForamt类中的getInstance()方法使用的是工厂模式；
>
> ​	2,Calendar类中的getInstance()方法使用的是工厂模式；



## 4.3 原型模式

### 4.3.1 概述

用一个已经创建的实例作为原型，通过复制该原型对象来创建一个和原型对象相同的新对象。

### 4.3.2 结构

原型模式包含如下角色：

* 抽象原型类：规定了具体原型对象必须实现的的 clone() 方法。
* 具体原型类：实现抽象原型类的 clone() 方法，它是可被复制的对象。
* 访问类：使用具体原型类中的 clone() 方法来复制新的对象。

接口类图如下：

![](img\原型模式.png)

### 4.3.3 实现

原型模式的克隆分为浅克隆和深克隆。

> 浅克隆：创建一个新对象，新对象的属性和原来对象完全相同，对于非基本类型属性，仍指向原有属性所指向的对象的内存地址。
>
> 深克隆：创建一个新对象，属性中引用的其他对象也会被克隆，不再指向原有对象地址。

Java中的Object类中提供了 `clone()` 方法来实现浅克隆。 Cloneable 接口是上面的类图中的抽象原型类，而实现了Cloneable接口的子实现类就是具体的原型类。代码如下：

**Realizetype（具体的原型类）：**

```java
/**
 * @version v1.0
 * @ClassName: Realizetype
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class Realizetype implements Cloneable {

    public Realizetype() {
        System.out.println("具体的原型对象创建完成！");
    }

    @Override
    public Realizetype clone() throws CloneNotSupportedException {
        System.out.println("具体原型复制成功！");

        return (Realizetype) super.clone();
    }
}
```

**PrototypeTest（测试访问类）：**

```java
public class PrototypeTest {
    public static void main(String[] args) throws CloneNotSupportedException {
        Realizetype r1 = new Realizetype();
        Realizetype r2 = r1.clone();

        System.out.println("对象r1和r2是同一个对象？" + (r1 == r2));
    }
}
```

### 4.3.4 案例

**用原型模式生成“三好学生”奖状**

同一学校的“三好学生”奖状除了获奖人姓名不同，其他都相同，可以使用原型模式复制多个“三好学生”奖状出来，然后在修改奖状上的名字即可。

类图如下：

<img src="img\原型模式1.png" style="zoom:80%;" />

代码如下：

Citation

```java
/**
 * @version v1.0
 * @ClassName: Citation
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class Citation implements Cloneable {

    //三好学生上的姓名
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Citation clone() throws CloneNotSupportedException {
        return (Citation) super.clone();
    }

    public void show() {
        System.out.println(stu.getName() + "同学：在2020学年第一学期中表现优秀，被评为三好学生。特发此状！");
    }
}

```

CitaionTest 

```java
/**
 * @version v1.0
 * @ClassName: CitaionTest
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public class CitaionTest {
    public static void main(String[] args) throws CloneNotSupportedException {
        //1，创建原型对象
        Citation citation = new Citation();
        //创建张三学生对象
        Student stu = new Student();
        stu.setName("张三");
        citation.setStu(stu);

        //2,克隆奖状对象
        Citation citation1 = citation.clone();

        citation.setName("张三");
        citation1.setName("李四");

        //3,调用show方法展示
        citation.show();
        citation1.show();
    }
}
```

### 4.3.5 使用场景

* 对象的创建非常复杂，可以使用原型模式快捷的创建对象。
* 性能和安全要求比较高。

### 4.3.6 扩展（深克隆）

将上面的“三好学生”奖状的案例中Citation类的name属性修改为Student类型的属性。代码如下：

```java
//奖状类
public class Citation implements Cloneable {
    private Student stu;

    public Student getStu() {
        return stu;
    }

    public void setStu(Student stu) {
        this.stu = stu;
    }

    void show() {
        System.out.println(stu.getName() + "同学：在2020学年第一学期中表现优秀，被评为三好学生。特发此状！");
    }

    @Override
    public Citation clone() throws CloneNotSupportedException {
        return (Citation) super.clone();
    }
}

//学生类
public class Student {
    private String name;
    private String address;

    public Student(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

//测试类
public class CitationTest {
    public static void main(String[] args) throws CloneNotSupportedException {

        Citation c1 = new Citation();
        Student stu = new Student("张三", "西安");
        c1.setStu(stu);

        //复制奖状
        Citation c2 = c1.clone();
        //获取c2奖状所属学生对象
        Student stu1 = c2.getStu();
        stu1.setName("李四");

        //判断stu对象和stu1对象是否是同一个对象
        System.out.println("stu和stu1是同一个对象？" + (stu == stu1));

        c1.show();
        c2.show();
    }
}
```

运行结果为：

<img src="img\原型模式2.png" style="zoom:80%;" />

<font color="red">说明：</font>

​	stu对象和stu1对象是同一个对象，就会产生将stu1对象中name属性值改为“李四”，两个Citation（奖状）对象中显示的都是李四。这就是浅克隆的效果，**对具体原型类（Citation）中的引用类型的属性进行引用的复制。这种情况需要使用深克隆，而进行深克隆需要使用对象流**。代码如下：

```java
public class CitationTest1 {
    public static void main(String[] args) throws Exception {
        Citation c1 = new Citation();
        Student stu = new Student("张三", "西安");
        c1.setStu(stu);

        //创建对象输出流对象
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Think\\Desktop\\b.txt"));
        //将c1对象写出到文件中
        oos.writeObject(c1);
        //关闭输出流
        oos.close();

        //创建对象出入流对象
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\Think\\Desktop\\b.txt"));
        //读取对象
        Citation c2 = (Citation) ois.readObject();
        //获取c2奖状所属学生对象
        Student stu1 = c2.getStu();
        stu1.setName("李四");

        //判断stu对象和stu1对象是否是同一个对象
        System.out.println("stu和stu1是同一个对象？" + (stu == stu1));

        c1.show();
        c2.show();
    }
}
```

运行结果为：

<img src="img\原型模式3.png" style="zoom:80%;" />

> 注意：Citation类和Student类必须实现Serializable接口，否则会抛NotSerializableException异常。



尚硅谷案例：

原型模式（Prototype Pattern）是用于创建重复的对象，同时又能保证性能。
本体给外部提供一个克隆体进行使用

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片8.png)

User

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Cloneable{
    private String username;
    private Integer age;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        User user = new User();
        user.setUsername(username);
        user.setAge(age);
        return user;
    }
}
```

GuliMybatis 

```java
/*
*TODO
* 是用于创建重复的对象，同时又能保证性能。
* 1、有一个框架MyBatis:操作数据库，从数据库里面查出很多记录(但大部分记录改变很少)
* 2、每次查数据库，查到以后把所有数据都封装一个对象，返回。
* 假设：有10000 thread:查一个记录: new User("zhanqsan",18);
*      每次创建一个对象封装并返回
*      内存里面里面就会有10000个User; 浪费内存
* 解决
*   有一个缓存 ，查过的数据(这个数据也是克隆)放入缓存
*   再查相同的记录 从内存中 拿到原型对象  再克隆原型 把克隆返回
* */
public class GuliMybatis {
    //TODO 设立一个缓存 查过的数据存入缓存
    HashMap cache = new HashMap<String,User>();

    //TODO 该方法模拟  传入用户名 从数据库查数据
    public User getUser(String username,Integer age) throws CloneNotSupportedException {
        if(!cache.containsKey(username)){
            User userFromDB = getUserFromDB(username,age);
            return userFromDB;
        }
        /*
        * TODO 从缓存直接拿到原型 不加以处理 可能导致脏缓存
        *  解决： clone原型
        * */
        User user;
        user = (User)cache.get(username);
        return (User) user.clone();
    }

    public User getUserFromDB(String username,Integer age) throws CloneNotSupportedException {
        System.out.println("从数据库查数据，并创建了对象:"+username);
        User user = new User(username, age);
        //TODO 把查询到的数据的克隆放入川村
        Object clone = user.clone();
        //TODO 查询到的数据放入缓存
        cache.put(username,clone);
        return user;
    }

}
```

 MainTest

```java
public class MainTest {
    /*
     *TODO
     * 是用于创建重复的对象，同时又能保证性能。
     * 1、有一个框架MyBatis:操作数据库，从数据库里面查出很多记录(但大部分记录改变很少)
     * 2、每次查数据库，查到以后把所有数据都封装一个对象，返回。
     * 假设：有10000 thread:查一个记录: new User("zhanqsan",18);
     *      每次创建一个对象封装并返回
     *      内存里面里面就会有10000个User; 浪费内存
     * 解决
     *   有一个缓存 ，查过的数据放入缓存
     *   再查相同的记录 从内存中 拿到原型对象 再克隆原型 把克隆返回
     * */
    public static void main(String[] args) throws CloneNotSupportedException {
        //TODO 模拟从数据库得到数据
        GuliMybatis guliMybatis = new GuliMybatis();
        //TODO 这里是clone
        User user1 = guliMybatis.getUser("张三",12);
        System.out.println("1==>"+user1);

        user1.setUsername("李四");
        System.out.println("张三自己改了名字："+user1);

        //TODO 这里是clone
        User user2 = guliMybatis.getUser("张三",12);
        System.out.println("2==>"+user2);

        user2.setUsername("王五");
        System.out.println("张三自己改了名字："+user2);

        //TODO 这里是clone
        User user3 = guliMybatis.getUser("张三",12);
        System.out.println("3==>"+user3);
    }
}
```

## 4.5 建造者模式

### 4.4.1 概述

将一个复杂对象的构建与表示分离，使得同样的构建过程可以创建不同的表示。

<img src="img/image-20200413225341516.png" style="zoom:60%;" />

* 分离了部件的构造(由Builder来负责)和装配(由Director负责)。 从而可以构造出复杂的对象。这个模式适用于：某个对象的构建过程复杂的情况。
* 由于实现了构建和装配的解耦。不同的构建器，相同的装配，也可以做出不同的对象；相同的构建器，不同的装配顺序也可以做出不同的对象。也就是实现了构建算法、装配算法的解耦，实现了更好的复用。
* 建造者模式可以将部件和其组装过程分开，一步一步创建一个复杂的对象。用户只需要指定复杂对象的类型就可以得到该对象，而无须知道其内部的具体构造细节。



### 4.4.2 结构

建造者（Builder）模式包含如下角色：

* 抽象建造者类（Builder）：这个接口规定要实现复杂对象的那些部分的创建，并不涉及具体的部件对象的创建。 

* 具体建造者类（ConcreteBuilder）：实现 Builder 接口，完成复杂产品的各个部件的具体创建方法。在构造过程完成后，提供产品的实例。 

* 产品类（Product）：要创建的复杂对象。

* 指挥者类（Director）：调用具体建造者来创建复杂对象的各个部分，在指导者中不涉及具体产品的信息，只负责保证对象各部分完整创建或按某种顺序创建。 

类图如下：

<img src="img/建造者模式.png" style="zoom:80%;" />



### 4.4.3 实例

**创建共享单车**

生产自行车是一个复杂的过程，它包含了车架，车座等组件的生产。而车架又有碳纤维，铝合金等材质的，车座有橡胶，真皮等材质。对于自行车的生产就可以使用建造者模式。

这里Bike是产品，包含车架，车座等组件；Builder是抽象建造者，MobikeBuilder和OfoBuilder是具体的建造者；Director是指挥者。类图如下：

<img src="img/建造者模式1.png" style="zoom:80%;" />

具体的代码如下：

```java
/**
 * @version v1.0
 * @ClassName: Bike
 * @Description: 产品对象
 * @Author: 黑马程序员
 */
public class Bike {

    private String frame;//车架

    private String seat;//车座

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
```

Builder

```java
/**
 * @version v1.0
 * @ClassName: Builder
 * @Description: TODO(一句话描述该类的功能)
 * @Author: 黑马程序员
 */
public abstract class Builder {

    //声明Bike类型的变量，并进行赋值
    protected Bike bike = new Bike();

    public abstract void buildFrame();

    public abstract void buildSeat();

    //构建自行车的方法
    public abstract Bike createBike();
}
```

MobileBuilder

```java
/**
 * @version v1.0
 * @ClassName: MobileBuilder
 * @Description: 具体的构建者，用来构建摩拜单车对象
 * @Author: 黑马程序员
 */
public class MobileBuilder extends Builder {

    public void buildFrame() {
        bike.setFrame("碳纤维车架");
    }

    public void buildSeat() {
        bike.setSeat("真皮车座");
    }

    public Bike createBike() {
        return bike;
    }
}
```



```java
/**
 * @version v1.0
 * @ClassName: OfoBuilder
 * @Description: ofo单车构建者，用来构建ofo单车
 * @Author: 黑马程序员
 */
public class OfoBuilder extends Builder {
    public void buildFrame() {
        bike.setFrame("铝合金车架");
    }

    public void buildSeat() {
        bike.setSeat("橡胶车座");
    }

    public Bike createBike() {
        return bike;
    }
}
```

 Director

```java
/**
 * @version v1.0
 * @ClassName: Director
 * @Description: 指挥者类
 * @Author: 黑马程序员
 */
public class Director {

    //声明builder类型的变量
    private Builder builder;

    public Director(Builder builder) {
        this.builder = builder;
    }

    //组装自行车的功能
    public Bike construct() {
        builder.buildFrame();
        builder.buildSeat();
        return builder.createBike();
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
        //创建指挥者对象
        Director director = new Director(new MobileBuilder());
        //让指挥者只会组装自行车
        Bike bike = director.construct();

        System.out.println(bike.getFrame());
        System.out.println(bike.getSeat());
    }
}
```

**注意：**

上面示例是 Builder模式的常规用法，指挥者类 Director 在建造者模式中具有很重要的作用，**它用于指导具体构建者如何构建产品，控制调用先后次序，并向调用者返回完整的产品类，但是有些情况下需要简化系统结构，可以把指挥者类和抽象建造者进行结合**

```java
// 抽象 builder 类
public abstract class Builder {

    protected Bike mBike = new Bike();

    public abstract void buildFrame();
    public abstract void buildSeat();
    public abstract Bike createBike();
    
    public Bike construct() {
        this.buildFrame();
        this.BuildSeat();
        return this.createBike();
    }
}
```

**说明：**

这样做确实简化了系统结构，但同时也加重了抽象建造者类的职责，也不是太符合单一职责原则，如果construct() 过于复杂，建议还是封装到 Director 中。



尚硅谷案例

产品角色（Product）：Phone
抽象建造者（Builder）：AbstracPhoneBuilder
具体建造者(Concrete Builder）：PhoneBuilder
创建的东西细节复杂，还必须暴露给使用者。屏蔽过程而不屏蔽细节

![](H:\笔记\黑马资料-java设计模式（图解+框架源码分析+实战）2020\day02\img\图片9.png)

AbstractBuilder

```java
//TODO 抽象层 建造者
public abstract class AbstractBuilder {
    Phone phone;
    //TODO 自定义手机的方法
    abstract void customCpu(String cpu);

    abstract void customMem(String mem);

    abstract void customDisk(String disk);

    abstract void customCamera(String camera);

    //TODO 返回产品
    Phone getProduct(){
        return phone;
    }
}
```

 Phone

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

    public String cpu;

    public String mem;

    public String disk;

    public String camera;

}
```

XiaoMiBuilder

```java
public class XiaoMiBuilder extends AbstractBuilder {

    public XiaoMiBuilder() {
        phone = new Phone();
    }

    @Override
    void customCpu(String cpu) {
        phone.cpu = cpu;
    }

    @Override
    void customMem(String mem) {
        phone.mem = mem;
    }

    @Override
    void customDisk(String disk) {
        phone.disk = disk;
    }

    @Override
    void customCamera(String camera) {
        phone.camera = camera;
    }
}
```

AbstractChainBuilder

```java
//TODO 抽象层 建造者
public abstract class AbstractChainBuilder {
    Phone phone;
    //TODO 自定义手机的方法
    abstract AbstractChainBuilder customCpu(String cpu);

    abstract AbstractChainBuilder customMem(String mem);

    abstract AbstractChainBuilder customDisk(String disk);

    abstract AbstractChainBuilder customCamera(String camera);

    //TODO 返回产品
    Phone getProduct(){
        return phone;
    }
}
```

XiaoMiChainBuilder 

```java
public class XiaoMiChainBuilder extends AbstractChainBuilder {

    public XiaoMiChainBuilder() {
        phone = new Phone();
    }

    @Override
    AbstractChainBuilder customCpu(String cpu) {
        phone.cpu = cpu;
        return this;//TODO 返回当前对象
    }

    @Override
    AbstractChainBuilder customMem(String mem) {
        phone.mem = mem;
        return this;//TODO 返回当前对象
    }

    @Override
    AbstractChainBuilder customDisk(String disk) {
        phone.disk = disk;
        return this;//TODO 返回当前对象
    }

    @Override
    AbstractChainBuilder customCamera(String camera) {
        phone.camera = camera;
        return this;//TODO 返回当前对象
    }
}
```

MainTest

```java
public class MainTest {
    public static void main(String[] args) {

        AbstractBuilder builder = new XiaoMiBuilder();
        //TODO 建造手机
        builder.customCpu("晓龙888");
        builder.customMem("16G");
        builder.customDisk("256G");
        builder.customCamera("1200万像素");
        //TODO 返回手机
        Phone product = builder.getProduct();

        System.out.println(product);

        AbstractChainBuilder chainBuilder = new XiaoMiChainBuilder();
        //TODO 建造手机
        Phone product2 = chainBuilder.customCpu("晓龙888")
                .customMem("16G")
                .customDisk("512G")
                .customCamera("4800万像素").getProduct();

        System.out.println(product2);
    }
}
```

### 4.4.4 优缺点

**优点：**

- 建造者模式的封装性很好。使用建造者模式可以有效的封装变化，在使用建造者模式的场景中，一般产品类和建造者类是比较稳定的，因此，将主要的业务逻辑封装在指挥者类中对整体而言可以取得比较好的稳定性。
- 在建造者模式中，客户端不必知道产品内部组成的细节，将产品本身与产品的创建过程解耦，使得相同的创建过程可以创建不同的产品对象。
- 可以更加精细地控制产品的创建过程 。将复杂产品的创建步骤分解在不同的方法中，使得创建过程更加清晰，也更方便使用程序来控制创建过程。
- 建造者模式很容易进行扩展。如果有新的需求，通过实现一个新的建造者类就可以完成，基本上不用修改之前已经测试通过的代码，因此也就不会对原有功能引入风险。符合开闭原则。

**缺点：**

造者模式所创建的产品一般具有较多的共同点，其组成部分相似，如果产品之间的差异性很大，则不适合使用建造者模式，因此其使用范围受到一定的限制。



### 4.4.5 使用场景

建造者（Builder）模式创建的是复杂对象，其产品的各个部分经常面临着剧烈的变化，但将它们组合在一起的算法却相对稳定，所以它通常在以下场合使用。

- 创建的对象较复杂，由多个部件构成，各部件面临着复杂的变化，但构件间的建造顺序是稳定的。
- 创建复杂对象的算法独立于该对象的组成部分以及它们的装配方式，即产品的构建过程和最终的表示是独立的。



### 4.4.6 模式扩展

建造者模式除了上面的用途外，**在开发中还有一个常用的使用方式，就是当一个类构造器需要传入很多参数时，如果创建这个类的实例，代码可读性会非常差，而且很容易引入错误，此时就可以利用建造者模式进行重构。**

重构前代码如下：

```java
public class Phone {
    private String cpu;
    private String screen;
    private String memory;
    private String mainboard;

    public Phone(String cpu, String screen, String memory, String mainboard) {
        this.cpu = cpu;
        this.screen = screen;
        this.memory = memory;
        this.mainboard = mainboard;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getMainboard() {
        return mainboard;
    }

    public void setMainboard(String mainboard) {
        this.mainboard = mainboard;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "cpu='" + cpu + '\'' +
                ", screen='" + screen + '\'' +
                ", memory='" + memory + '\'' +
                ", mainboard='" + mainboard + '\'' +
                '}';
    }
}

public class Client {
    public static void main(String[] args) {
        //构建Phone对象
        Phone phone = new Phone("intel","三星屏幕","金士顿","华硕");
        System.out.println(phone);
    }
}
```

上面在客户端代码中构建Phone对象，传递了四个参数，如果参数更多呢？代码的可读性及使用的成本就是比较高。

重构后代码：

```java
public class Phone {

    private String cpu;
    private String screen;
    private String memory;
    private String mainboard;

    private Phone(Builder builder) {//私有构造函数 让调用者使用builder构造对象
        cpu = builder.cpu;
        screen = builder.screen;
        memory = builder.memory;
        mainboard = builder.mainboard;
    }
	//内部类
    //建造者的属性和产品的属性一致 ☆☆☆☆☆☆
    public static final class Builder {//final可以确保没有子类
        private String cpu;
        private String screen;
        private String memory;
        private String mainboard;

        public Builder() {}

        public Builder cpu(String val) {
            cpu = val;
            return this;
        }
        public Builder screen(String val) {
            screen = val;
            return this;
        }
        public Builder memory(String val) {
            memory = val;
            return this;
        }
        public Builder mainboard(String val) {
            mainboard = val;
            return this;
        }
         //使用构建者创建Phone对象
        public Phone build() {
            return new Phone(this);}
    }
    @Override
    public String toString() {
        return "Phone{" +
                "cpu='" + cpu + '\'' +
                ", screen='" + screen + '\'' +
                ", memory='" + memory + '\'' +
                ", mainboard='" + mainboard + '\'' +
                '}';
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
        //创建手机对象   通过构建者对象获取手机对象
        Phone phone = new Phone.Builder()
                .cpu("intel")
                .screen("三星屏幕")
                .memory("金士顿内存条")
                .mainboard("华硕主板")
                .build();

        System.out.println(phone);
    }
}
```

**重构后的代码在使用起来更方便，某种程度上也可以提高开发效率。从软件设计上，对程序员的要求比较高**。



## 4.6 创建者模式对比

### 4.6.1 工厂方法模式VS建造者模式

工厂方法模式注重的是整体对象的创建方式；而建造者模式注重的是部件构建的过程，意在通过一步一步地精确构造创建出一个复杂的对象。

我们举个简单例子来说明两者的差异，如要制造一个超人，如果使用工厂方法模式，直接产生出来的就是一个力大无穷、能够飞翔、内裤外穿的超人；而如果使用建造者模式，则需要组装手、头、脚、躯干等部分，然后再把内裤外穿，于是一个超人就诞生了。

### 4.6.2 抽象工厂模式VS建造者模式

抽象工厂模式实现对产品家族的创建，一个产品家族是这样的一系列产品：具有不同分类维度的产品组合，采用抽象工厂模式则是不需要关心构建过程，只关心什么产品由什么工厂生产即可。

建造者模式则是要求按照指定的蓝图建造产品，它的主要目的是通过组装零配件而产生一个新产品。

如果将抽象工厂模式看成汽车配件生产工厂，生产一个产品族的产品，那么建造者模式就是一个汽车组装工厂，通过对部件的组装可以返回一辆完整的汽车。

