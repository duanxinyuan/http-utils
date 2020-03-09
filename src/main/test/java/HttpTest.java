import com.fasterxml.jackson.core.type.TypeReference;
import com.dxy.library.json.jackson.JacksonUtil;
import com.dxy.library.network.http.Http;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.header.Headers;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.time.Clock;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author duanxinyuan
 * 2018/8/4 15:18
 */
public class HttpTest {
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void testChinese() {
        Headers headers = new Headers("aaa", "您你");
        System.out.println(Http.enableRequestLog().timeout(300).retries(3).get("https://www.baidu.com", headers));
    }

    /**
     * 测试用byte数组接收数据
     */
    @Test
    public void testHttpBytes() {
        byte[] bytes = Http.timeout(300).get("https://www.baidu.com", byte[].class);
        System.out.println(new String(bytes));
    }

    /**
     * 测试用流接收数据
     */
    @Test
    public void testHttpInputStream() throws IOException {
        InputStream inputStream = Http.get("https://www.baidu.com", InputStream.class);
        System.out.println(inputStream.read());

        Reader reader = Http.get("https://www.baidu.com", Reader.class);
        System.out.println(reader.read());
    }

    @Test
    public void testHttp() {
        System.out.println(Http.enableRequestLog().timeout(300).get("https://www.baidu.com"));
        System.out.println(Http.disableRequestLog().timeout(300).get("https://www.baidu.com"));
        System.out.println(Http.enableRequestLog().retries(3).timeout(300).get("https://www.baidu.com"));
        System.out.println(Http.enableRequestLog().retries(3, 30000).timeout(300).get("https://www.baidu.com"));
        System.out.println(Http.retries(3).timeout(300).requestLogEnable(true).get("https://www.baidu.com"));
        System.out.println(Http.retries(3).timeout(300).disableRequestLog().get("https://www.baidu.com"));

        Http.setDefaultTimeout(300);
        Http.setDefaultRequestLogEnable(false);
        System.out.println(Http.get("https://www.baidu.com"));
        Http.setDefaultRequestLogEnable(true);
        System.out.println(Http.get("http://www.baidu.com"));
    }

    @Test
    public void testGetAsync() throws InterruptedException {
        Http.getAsync("http://www.baidu.com", new RequestCallback() {
            @Override
            public void success(String s) {
                System.out.println(s);
                countDownLatch.countDown();
            }

            @Override
            public void failure(String s) {
                System.out.println(s);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }



}
