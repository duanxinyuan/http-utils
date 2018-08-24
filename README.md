# Network
包含Http、Netty、AKKA的封装


## Maven依赖：
Http：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>network-http</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Http使用示例：
```java
Headers headers=new Headers("token","").add("key","").add("k1","v1","k2","v2","k3","v3");
Params params=new Params("name","").add("key","").add("k1","v1","k2","v2","k3","v3");

1、GET：
      //同步请求
      String s = Http.get(url, headers, params);
      Result result = Http.get(url, headers, params, Result.class);

      //异步请求
      Http.getAsync(url, headers, params, callback);

2、POST：
      //同步请求：
      String s = Http.post(url, headers, params);
      Result result = Http.post(url, headers, params, Result.class);

      //异步请求：
      Http.postAsync(url, headers, params, callback);

      //POST同步提交JSON：
      String s = Http.postJson(url, headers, params, testBean);
      Result result = Http.postJson(url, testBean, Result.class);

      //POST异步提交JSON：
      Http.postJsonAsync(url, testBean, callback);

3、PUT：
       //同步请求：
      String s = Http.put(url, headers, params);
      Result result = Http.put(url, headers, params, Result.class);

      //异步请求：
      Http.putAsync(url, headers, params, callback);

      //PUT同步提交JSON：
      String s = Http.putJson(url, headers, params, testBean);
      Result result = Http.putJson(url, testBean, Result.class);

      //PUT异步提交JSON：
      Http.putJsonAsync(url, testBean, callback);

4、PATCH：
      //同步请求
      String s = Http.patch(url, headers, params);
      Result result = Http.patch(url, headers, params, Result.class);

      //异步请求
      Http.patchAsync(url, headers, params, callback);       

4、DELETE：
      //同步请求
      String s = Http.delete(url, headers, params);
      Result result = Http.delete(url, headers, params, Result.class);

      //异步请求
      Http.deleteAsync(url, headers, params, callback);       

5、DOWNLOAD：
      //同步下载，targetPath为本地文件存储地址
      Http.download(url,targetPath);

      //异步下载，targetPath为本地文件存储地址
      Http.downloadAsync(url,targetPath);
      
  关闭日志请求：
  Http.disableLog().get(url, headers, params);
 
  开启日志请求：
  Http.enableLog().get(url, headers, params);
      
  屏蔽日志：
  Http.blockLog();
         
  解除日志屏蔽：
  Http.unblockLog();

```
