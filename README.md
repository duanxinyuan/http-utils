# Network Utils
网络工具类，包含OKHttp、TCP、Netty、AKKA的封装


## Maven依赖：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>library-network</artifactId>
    <version>1.0.0</version>
</dependency>
```


## OKHttp使用示例：
```java
  Headers headers=new Headers("token","").add("key","").add("k1","v1","k2","v2","k3","v3");
  Params params=new Params("name","").add("key","").add("k1","v1","k2","v2","k3","v3");
  String s = OkHttpUtil.get(url, headers, params);
```
