package javametrics;


import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/17 15:24
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  Counter 就是计数器，Counter 只是用 Gauge 封装了 AtomicLong 。
 */
public class CounterTest {

    public static Queue<String> q = new LinkedList<String>();

    public static Counter counter;

    public static Random random = new Random();

    public static void addJob(String job){
        counter.inc();
        q.offer(job);
    }

    public static String takeJob(){
        counter.dec();
        String poll = q.poll();
        return poll;
    }

    public static void main(String[] args) {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        counter = registry.counter(MetricRegistry.name(Queue.class, "job", "size"));
        int num = 1;
        while (true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (random.nextDouble() > 0.7){
                String takeJob = takeJob();
                System.out.println("拿出的job为:" + takeJob);
            }else {
                String job = "job" + num;
                addJob(job);
                System.out.println("添加的job为;" + job);
            }
            num++;
        }
    }
}
