# Lambda 表达式学习

#### Java8 新特性 -stream 流的使用(内部迭代)

几乎所有的lambda的表达式都依赖于stream

**惰性求值**：当所有的操作都是对于stream的一种描述，也就是所有的操作，返回的值都是stream就是一种惰性求值，它不会这么快的进行循环迭代，当最后调用过早求值的时候，这样才会进行循环迭代将结果给返回。 借助于惰性求值，可以减少不必要的循环，增加了程序的性能。

**过早求值**：将想要的结果返回，不返回steam的结果都是过早求值，过早求值之后的stream不能再继续使用

---

#### 过滤 filter

```java
// 将返回的结果过滤为 符合 predicate的元素
// predicate是一个接受 元素，然后返回 boolean 的函数式接口
Stream<T> filter(Predicate<? super T> predicate);


// 例子
Stream<String> a = Stream.of("a", "b", "c", "d");
a.filter(u -> u.compareTo("a") > 0).forEach(System.out::println);

// 输出
b
c
d
```

---

#### 映射(投影)  map   

map可以用数学上的说法 是一个投影，可以将stream的元素 转换为另一种元素

---

map可以有一些简单的用法

将小写的字符串转换为大写

将所有的对象转换为另一种对象

将所有的元素都累加一个值

```java
    /**
     * 将所有元素转换为大写
     */
    public static void mapToUpperCase() {
        Stream<String> a = Stream.of("a", "b", "c", "d");
        a.map(u -> u.toUpperCase(Locale.ENGLISH)).forEach(System.out::println);
    }
// 输出
A
B
C
D
---
    /**
     * 将所有的数字都添加17
     */
    public static void mapToAddNum() {
        IntStream integerStream = IntStream.of(1, 2, 3, 4);
        integerStream.map(u->u+17).forEach(System.out::println);
    }
```

---

#### flatMap 将一连串的stream 改为一个stream

当我们的 collection为 List<List\<Integer>> 的时候，调用 .stream() 会生成**stream\<stream\<Integer>>** 对象 

不利于后面的使用，因此可以调用flatMap来 将stream\<stream\<Integer>>改为 stream\<Integer> 



```java
public static void main(String[] args) {
    Stream<List<Integer>> listStream = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4, 5));
    // 在这里将降阶的stream转换为 list,没有降阶的stream是不能转换为list的
    List<Integer> collect = listStream.flatMap(u -> u.stream()).collect(Collectors.toList());
    System.out.println(collect);
}
```

---

#### min max 获取stream里面的 最大的元素，获取最小的元素

通过stream，我们可以很方便的获取 自己定义出来的最大的元素，和最小的元素，非常方便用于找最大值和最小值。

```java
// 查找User里面 name的最小值，这里会调用 String的compareTo方法
public static void getMinValue() {
    Stream<User> userStream = Stream.of(new User(1, "1"), new User(2, "2"), new User(3, "3"));
    Optional<User> min = userStream.min(Comparator.comparing(User::getName));
    System.out.println(min.get());
}

--- 
User(id=1, name=1)
// 查找 id最大的User
public static void getMaxValue() {
    Stream<User> userStream = Stream.of(new User(1, "1"), new User(2, "2"), new User(3, "3"));
    Optional<User> min = userStream.max(Comparator.comparing(User::getId));
    System.out.println(min.get());
}

--
User(id=3, name=3)
```

---

#### reduce 操作

reduce 其实是一个将初始值，和stream的元素值互相操作，然后返回最终结果的值。

它接受3个参数

第一个参数为 **初始值**

第二个参数为 中间值和初始值作为参数，生成中间结果的函数式子

第三个参数 为生成多个的容易 进行合并的 函数式子

一般只要两个参数即可

```java
// 分别用三个参数的reduce 来实现 1+2+3

    /**
     * 这里没有初始值，它会将第一个元素作为初始值来进行后面的计算
     */
    public static void reduceWithOneParameter() {
        IntStream range = IntStream.range(1, 4);
        OptionalInt reduce = range.reduce((a, b) -> a + b);
        System.out.println(reduce.getAsInt());
    }

    /**
     * 这里定义了初始值，然后再进行计算
     */
    public static void reduceWithTwoParameter() {
        IntStream range = IntStream.range(1, 4);
        int reduce = range.reduce(0, (a, b) -> a + b);
        System.out.println(reduce);
    }

    /**
     * 这里有三个参数，第一个是初始值，第二个是合并两个元素的操作，第三个是合并两个集合的操作
     */
    public static void reduceWithThreeParameter() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3);
        Integer reduce = integerStream.reduce(0, (a, b) -> a + b, (co1, co2) -> co1 + co2);
        System.out.println(reduce);
    }
```



---

#### 通过summaryStatistics 方法来进行统计

使用这个方法，可以获取 **max** ，**min**,**average** 和 **sum**

这样就不用循环多次。一次就可以将想要的值全部都给统计出来

```java
    /**
     * 这里注意一下，只有IntStream DoubleStream 等等基本数据类型的 stream才能获取 统计数据IntSummaryStatistics
     */
    public static void intSummary() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7);
        IntSummaryStatistics intSummaryStatistics = integerStream.mapToInt(u -> u).summaryStatistics();
        System.out.println(intSummaryStatistics.getMax());
        System.out.println(intSummaryStatistics.getMin());
        System.out.println(intSummaryStatistics.getCount());
        System.out.println(intSummaryStatistics.getAverage());
        System.out.println(intSummaryStatistics.getSum());
    }

```

---

#### sorted 排序

使用sorted这个方法，很方便我们对函数进行排序

```java
    /**
     * 通过id的大小进行排序
     */
    public static void sortedById() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        List<User> collect = userStream.sorted(Comparator.comparingInt(User::getId)).collect(Collectors.toList());
        System.out.println(collect);
    }
```

---



#### max，min

对于非基本数据类型的对象，我们可以用这两个函数来定义哪个类的值比较大，然后取得最大值，最小值

```java
    /**
     * 自定义的方式获取最大值和最小值
     */
    public static void maxById() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        Optional<User> max = userStream.max((u1, u2) -> u2.getId() - u1.getId());
        System.out.println(max.get());
    }
这里为了区别化，我们将 id大的定义为小，id小的定义为大
```

---

#### toCollection 将stream 转换为指定的集合

使用stream 经常会用到**toList**方法 ，但是这样没法实际的生成我们想要的list，或者set

通过 toCollection方法，我们可以定制生成的集合

例如生成 Set，TreeSet,CopyOnWriteList 等集合

```java
    /**
     * 转换为 hashSet
     */
    public static void toHashSet() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        HashSet<User> collect = userStream.collect(Collectors.toCollection(HashSet::new));
        System.out.println(collect);
    }

    /**
     *  转换为copyOnWriteList
     */
    public static void toCopyOnWriteList() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        CopyOnWriteArrayList<User> collect = userStream.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        System.out.println(collect);
    }
```

---

#### groupingBy 对数据进行分组，转换为 map<分组值，元素>

很多时候，我们需要对数据进行分组

比如对于学生来说，我们可以分为男子组，女子组

或者可以分为 一年级，二年级，三年级等等

可以借助 groupingBy的方法进行数据分组

```java
    /**
     * 通过年级进行分组
     */
    public static void groupByGrade() {
        Stream<Student> studentStream = Stream.of(new Student(1, "学生1", "一年级"), new Student(2, "学生2", "一年级"), new Student(3, "学生3", "三年级"), new Student(4, "学生4", "二年级"));
        Map<String, List<Student>> collect = studentStream.collect(Collectors.groupingBy(Student::getGrade));
        System.out.println(collect);
    }
```

