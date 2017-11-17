
import org.apache.zookeeper.*;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/17 10:34
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description: Demo 实现一种同步的分布式队列，当定义的队列成员都聚齐时，这个队列才可用，否则一直等待所有成员到达。
 */

public class ZkQueueDemo {

    public static void main(String[] args) throws Exception {
        // 模拟app1通过zk1提交x1,app2通过zk2提交x2,app3通过zk3提交x3
        doAction(1);
        doAction(2);
        doAction(3);
    }

    /**
     * 以此集群的3台机器上加入某成员
     *
     * @param client
     * @throws Exception
     */
    public static void doAction(int client) throws Exception {

        // zookeeper 服务器的连接地址
        String host1 = "nowledgedata-n238:2181";
        String host2 = "nowledgedata-n239:2181";
        String host3 = "nowledgedata-n240:2181";
        ZooKeeper zk = null;
        switch (client) {
            case 1:
                // 初始化连接
                zk = connection(host1);
                // 初始化队列
                initQueue(zk);
                // 加入节点
                joinQueue(zk, 1);
                break;
            case 2:
                zk = connection(host2);
                initQueue(zk);
                joinQueue(zk, 2);
                break;
            case 3:
                zk = connection(host3);
                initQueue(zk);
                joinQueue(zk, 3);
                break;
        }
    }

    /**
     * 连接服务器zookeeper连接
     *
     * @param host
     * @return
     * @throws Exception
     */
    public static ZooKeeper connection(String host) throws Exception {
        ZooKeeper zk = new ZooKeeper(host, 60000, new Watcher() {
            // 用来监控所有被触发的事件
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeCreated && event.getPath().equals("/queue/start")) {
                    System.out.println("Queue has Completed.Finish testing!!!");
                }
            }
        });
        return zk;
    }

    /**
     * 初始化队列
     *
     * @param zk
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void initQueue(ZooKeeper zk) throws KeeperException, InterruptedException {
        System.out.println("WATCH => /queue/start");

        // 当这个znode节点被改变时，将会触发当前Watcher
        zk.exists("/queue/start", true);

        // 如果/queue目录为空，创建此节点
        if (zk.exists("/queue", false) == null) {
            System.out.println("create /queue task-queue");
            zk.create("/queue", "task-queue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            System.out.println("/queue is exist!");
        }

        // 如果 /queue/start 目录存在，则先删除
        if ( zk.exists("/queue/start", false) != null){
            zk.delete("/queue/start", -1);
        }

    }

    /**
     * 成员加入队列
     *
     * @param zk
     * @param x
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void joinQueue(ZooKeeper zk, int x) throws KeeperException, InterruptedException {

        System.out.println("create /queue/x" + x + " x" + x);
        zk.create("/queue/x" + x, ("x" + x).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        isCompleted(zk);

    }

    /**
     * 判断队列是否已满
     *
     * @param zk
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void isCompleted(ZooKeeper zk) throws KeeperException, InterruptedException {

        //规定队列大小
        int size = 3;
        //查询成员数
        int length = zk.getChildren("/queue", true).size();
        System.out.println("Queue Complete:" + length + "/" + size);
        if (length >= size) {
            System.out.println("create /queue/start start");
            zk.create("/queue/start", "start".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }
}
