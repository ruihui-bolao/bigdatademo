package javametrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/17 15:09
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   最简单返回值，可以用来衡量一个待处理队列中任务的个数
 */
public class GaugeTest {

    public static Queue<String> q = new LinkedList<String>();

    public static void main(String[] args) {

        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        registry.register(MetricRegistry.name(GaugeTest.class, "queue", "size"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return q.size();
            }
        });
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            q.add("job-xxx");
        }
    }



}
