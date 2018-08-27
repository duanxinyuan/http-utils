import com.dxy.library.network.http.Http;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/4 15:18
 */
public class HttpTest {

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

}
