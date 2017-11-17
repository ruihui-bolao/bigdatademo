import com.sdyc.springjms.demo.ConsumerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/23 11:15
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class SyncConsumerActiveMQTest {
    @Resource
    private ConsumerService cService;

    @Resource(name="queueDestination")
    private Destination receiveQueue;

    @Resource(name="responseQueue")
    private Destination replyQueue;

    @Test
    public void producerTest(){
        String result = cService.receiveMessage(receiveQueue, replyQueue);
        System.out.println(result);
    }
}
