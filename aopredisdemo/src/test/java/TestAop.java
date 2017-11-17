import com.hui.aopredis.demo.service.TextManagerImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 16:00
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */


public class TestAop {

    @Test
    public void test2(){
        DateTime now = DateTime.now();
        System.out.println(now.toString());
        System.out.println(now.minusMinutes(-10).toString());
    }
}
