import com.sdyc.springjms.demo.ProducerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/23 11:12
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class SyncProducerActiveMQTest {
    @Resource
    private ProducerService pService;

    @Resource(name="queueDestination")
    private Destination receiveQueue;

    @Test
    public void producerTest(){
        pService.sendMessage(receiveQueue, "我的名字!");
    }
}
