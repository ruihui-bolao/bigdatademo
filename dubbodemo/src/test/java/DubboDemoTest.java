import com.crh.dubbo.demo.service.DubboService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/9 10:51
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)// 添加spring测试方案
@ContextConfiguration("classpath:dubboConsumer.xml")// 指定spring配置文件位置
public class DubboDemoTest {
    @Resource
    protected DubboService dubboService;

    @Test
    public void outPrint() {
        String str = dubboService.demoService("hui");
        System.out.println(str);
    }
}
