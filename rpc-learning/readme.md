# rpc 手动实现方式

首先创建一个生产者，一个消费者

生产者 实现了 

1. 注册 生产者
2. 实现 生产者任务 ： 这里的源码 是获取 所有的 反射需要的类 还有 方法，进行实例化对象，调用消费者需要的方法，并返回结果

```java
/**
 * 我实现的 RPC 生产者
 * 改进了书本的错误例子 生产者 维护了 <b>接口</b>和<b>实现类</b>
 * 消费者只需要维护 <b>接口</b>
 * @author grayRainbow
 */
public class MyRpcExporter {
    /**
     * 创建线程池 用于 并发 使用
     * 生产者里面的所有类都可以进行 RPC调用
     */
    static Executor executor = Executors.newFixedThreadPool(10);

    /**
     * 注册生产者
     * @param hostName IP地址 可以为localhost
     * @param port 端口
     * @throws IOException
     */
    public static void exporter(String hostName, int port) throws IOException {
        // 注册生产者
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                // 调用生产者任务
                executor.execute(new MyRpcExporter.ExporterTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }

    /**
     * 生产者任务 实现
     */
    private static class ExporterTask implements Runnable {

        Socket client = null;

        /**
         * 接受一个socket 客户端
         * @param client
         */
        public ExporterTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream())) {
                // 获取被调用的接口名
                String interfaceName = inputStream.readUTF();
                // 加载类
                Class<?> service = Class.forName(interfaceName);

                // 获取子实现类
                Class subImpletementClass = ReflectUtils.getSubImpletementClass(service);

                // 获取调用的方法
                String methodName = inputStream.readUTF();
                // 获取参数类型列表
                Class<?>[] parameter = (Class<?>[]) inputStream.readObject();
                // 获取 传入的参数
                Object[] arg = (Object[]) inputStream.readObject();
                // 利用反射 获取方法
                Method method = subImpletementClass.getMethod(methodName, parameter);
                // 直接实例化一个对象 然后 调用方法
                Object result = method.invoke(subImpletementClass.newInstance(), arg);
                // 这一步进行  返回 方法执行 结果
                try (ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream())) {
                    outputStream.writeObject(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

消费者作用

1. 维护 **生产者 接口** 这边有个缺陷就是 接口的包名 一定要和生产者的包名**相同**
2. JDK动态代理 接口 ,将所有的方法给拦截了，全部 远端调用 生产者的方法

```java
public class MyRpcImporter<S> {

    /**
     * 自己改善的 RPC 方法，真正的不需要实现类的接口，只需要传入 接口即可
     * 不过这个还是 有问题的，比如有多个实现类的时候，并没有指明用哪个，所以后期还是可以改善一下
     * @param interfaces 接口的class
     * @param addr sock地址
     * @return
     * @throws ClassNotFoundException
     */
    public S importer(final Class<?> interfaces, final InetSocketAddress addr) throws ClassNotFoundException {

        return (S) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class<?>[]{interfaces}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket();
                socket.connect(addr);
                try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    // 作为消费者 只维护了接口 因此只能传接口名称过去 让那边加载类，然后再 寻找实现类
                    outputStream.writeUTF(interfaces.getName());
                    // 传入 要调用的方法
                    outputStream.writeUTF(method.getName());
                    // 传入 参数类型
                    outputStream.writeObject(method.getParameterTypes());
                    // 传入 入参
                    outputStream.writeObject(args);
                    // 获取结果
                    try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                        return inputStream.readObject();
                    }
                }
            }
        });
    }


}
```



被调用的接口

```java
public interface EchoService {
    String echo(String ping);
}
```



生产者接口实现类

```java
public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String ping) {
        return ping != null ? ping + " --> I am ok." : "I am ok.";
    }
}
```



查找接口的 全部实现子类工具类

```java
public class ReflectUtils {
    /**
     * 根据接口查找实现类
     * 这个依赖了一个 maven 依赖
     * <dependency>
     *     <groupId>org.reflections</groupId>
     *     <artifactId>reflections</artifactId>
     *     <version>0.9.11</version>
     * </dependency>
     * @param interfaceClass
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static<T> Class getSubImpletementClass(Class<T> interfaceClass) throws ClassNotFoundException {
        // 这里定义要扫描的包名
        Reflections reflections = new Reflections(interfaceClass.getPackage().getName());
        // 查找所有的接口 实现类
        Set<Class<? extends T>> classes = reflections.getSubTypesOf(interfaceClass);
        for (Iterator<Class<? extends T>> iterator = classes.iterator(); iterator.hasNext(); ) {
            Class<? extends T> next = iterator.next();
            System.out.println(next.getName());
            return next;
        }
        throw new ClassNotFoundException(interfaceClass.getName()+"not found implements.");
    }
}
```



测试方法如下

```java
public class MyRpcTest {
    /**
     * 书上的例子
     * 这个是假的Rpc调用，依赖于 实现类的调用，也就是本身就有这个类了，那么还要远端调用干嘛
     * @param args
     */
    public static void main(String[] args) throws ClassNotFoundException {
        // 注册生产者
        new Thread(() -> {
            try {
                MyRpcExporter.exporter("localhost", 8088);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 注册消费者，然后实例化消费者
        MyRpcImporter<EchoService> importer = new MyRpcImporter<>();

        // 这里使用了 JDK的动态代理，将所有的方法执行都给拦截了，拦截后 进行远端调用
        // 不过这里是个假的实现，发现没有 <b>实现<b/>类直接作为参数进行 传参，那么 就不是 Rpc调用
        EchoService echo = importer.importer(EchoService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(echo.echo("hello."));
    }

}
```



控制台输出

```tex
E:\software\Java\jdk64\jdk1.8.0_162\bin\java.exe -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:56162,suspend=y,server=n -javaagent:C:\Users\grayRainbow\AppData\Local\JetBrains\IntelliJIdea2020.1\captureAgent\debugger-agent.jar -Dfile.encoding=UTF-8 -classpath "E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\charsets.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\deploy.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\access-bridge-64.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\cldrdata.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\dnsns.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\jaccess.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\jfxrt.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\localedata.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\nashorn.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\sunec.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\sunjce_provider.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\sunmscapi.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\sunpkcs11.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\ext\zipfs.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\javaws.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\jce.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\jfr.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\jfxswt.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\jsse.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\management-agent.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\plugin.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\resources.jar;E:\software\Java\jdk64\jdk1.8.0_162\jre\lib\rt.jar;K:\programing\java\intellij\templates\java-templates\rpc-learning\target\classes;K:\maven\repository\org\reflections\reflections\0.9.11\reflections-0.9.11.jar;K:\maven\repository\com\google\guava\guava\20.0\guava-20.0.jar;K:\maven\repository\org\javassist\javassist\3.21.0-GA\javassist-3.21.0-GA.jar;E:\software\IntelliJ IDEA 2020.1\lib\idea_rt.jar" com.hkey.rpc.learn.sample.test.MyRpcTest
Connected to the target VM, address: '127.0.0.1:56162', transport: 'socket'
com.hkey.rpc.learn.sample.service.EchoServiceImpl
hello. --> I am ok.
Disconnected from the target VM, address: '127.0.0.1:56162', transport: 'socket'

Process finished with exit code 130
```

