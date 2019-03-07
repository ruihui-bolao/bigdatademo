package dubbo.consumer;

import dubbo.entity.Student;
import dubbo.producer.service.DubboService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/9 10:51
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class DubboDemoConsumer {

    protected DubboService dubboService;

    public DubboDemoConsumer(DubboService dubboService) {
        this.dubboService = dubboService;
    }

    public void loadDubbo() throws Exception{
        Student student = new Student();
        student.setName("hui");
        student.setCity("taiyuan");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("temp", student);
        String result = dubboService.demoService(map);
        System.out.println(result);
    }


    public static void main(String[] args) throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubboConsumer.xml");
        DubboService dubboService = (DubboService) context.getBean("dubboService");
        DubboDemoConsumer consumer = new DubboDemoConsumer(dubboService);
        consumer.loadDubbo();
    }
}
