package javametrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/17 16:03
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  Timer其实是 Histogram 和 Meter 的结合
 */
public class TimerTest {

    public static Random random = new Random();

    public static void main(String[] args) {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        Timer timer = registry.timer(MetricRegistry.name(TimerTest.class, "get"));
        Timer.Context ctx;
        while (true){
            ctx = timer.time();
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.stop();
        }
    }


}
