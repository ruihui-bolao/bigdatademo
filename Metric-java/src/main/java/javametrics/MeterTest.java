package javametrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/17 15:41
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   Meter度量一系列事件发生的速率(rate)，例如TPS。Meters会统计最近1分钟，5分钟，15分钟，还有全部时间的速率
 */
public class MeterTest {

    public static Random random = new Random();

    public static void request(Meter meter){
        System.out.println("request");
        meter.mark();
    }

    public static void request(Meter meter, int n){
        while (n>0){
            request(meter);
            n--;
        }
    }

    public static void main(String[] args) {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        Meter meter = registry.meter(MetricRegistry.name(MeterTest.class, "request", "tps"));
        while (true){
            request(meter,random.nextInt(5));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
