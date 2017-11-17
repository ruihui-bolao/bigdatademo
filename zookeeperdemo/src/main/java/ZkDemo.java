import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/13 11:20
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description: 测试zookeeper连接，（获取所有节点的列表，获取所有节点的状态信息）
 */
public class ZkDemo {

    private static CuratorFramework clientOne() {
        //zk 地址
        String connectString = "192.168.1.101:2181";
        // 连接时间 和重试次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        client.start();
        return client;
    }

    private static List<String> nodesList(CuratorFramework client, String parentPath) throws Exception {
        List<String> paths = client.getChildren().forPath(parentPath);
        return paths;
    }

    public static Stat getStat(CuratorFramework client, String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat;
    }


    public static void main(String[] args) {
        CuratorFramework curatorFramework = clientOne();
        try {
            List<String> strings = nodesList(curatorFramework, "/");
            Stat stat = getStat(curatorFramework, "/live_nodes");
            for (String string : strings) {
                System.out.println(string);
            }
            System.out.println(stat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
