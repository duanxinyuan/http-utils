import com.dxy.library.network.http.Http;
import com.dxy.library.network.http.callback.RequestCallback;
import com.dxy.library.network.http.param.FileParam;
import com.dxy.library.network.http.param.Params;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Clock;
import java.util.concurrent.CountDownLatch;

/**
 * @author duanxinyuan
 * 2018/8/4 15:18
 */
public class HttpTest {
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void testHttp() {
        System.out.println(Http.enablelog().timeout(300).get("http://www.baidu.com"));
        System.out.println(Http.disableLog().timeout(300).get("http://www.baidu.com"));
        Http.blockLog();
        System.out.println(Http.get("http://www.baidu.com"));
        Http.unblockLog();
        System.out.println(Http.get("http://www.baidu.com"));
        Http.timeout(300);
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

    @Test
    public void testPostFile() throws InterruptedException {
        Params params = new Params();
        params.add("module", "test");
        params.add("path", "user/test/test_" + Clock.systemUTC().millis());
        params.add("expireDays", 3);
        URL url = HttpTest.class.getClassLoader().getResource("test.txt");
        assert url != null;
        FileParam fileParam = new FileParam("file", new File(url.getFile()));
        String postFile = Http.postFile("http://127.0.0.1:20101/fs/upload", params, fileParam);
        System.out.println(postFile);
        System.out.println(" http://127.0.0.1:20101/fs/download?module=mobeye&path=" + params.get("path"));

        InputStream inputStream = HttpTest.class.getClassLoader().getResourceAsStream("test.txt");
        FileParam fileParam1 = new FileParam("file", inputStream);
        params.add("path", "user/test/test_" + Clock.systemUTC().millis());
        System.out.println(" http://127.0.0.1:20101/fs/download?module=mobeye&path=" + params.get("path"));
        String postFile1 = Http.postFile("http://127.0.0.1:20101/fs/upload", params, fileParam1);
        System.out.println(postFile1);

        InputStream inputStream2 = HttpTest.class.getClassLoader().getResourceAsStream("test.txt");
        FileParam fileParam2 = new FileParam("file", inputStream2);
        params.add("path", "user/test/test_" + Clock.systemUTC().millis());
        Http.postFileAsync("http://127.0.0.1:20101/fs/upload", params, fileParam2, new RequestCallback() {
            @Override
            public void success(String s) {
                System.out.println(s);
                System.out.println(" http://127.0.0.1:20101/fs/download?module=mobeye&path=" + params.get("path"));
                countDownLatch.countDown();
            }

            @Override
            public void failure(String s) {
                System.out.println("postFileAsync error");
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
