package Berkeley.demo;

import com.sleepycat.je.*;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/18 11:37
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class BerkeleyDemo {

    /**
     * berkeley数据库的开发环境
     */
    static private Environment env;

    /**
     * berkeley 库名
     */
    private Database db;

    static {
        String path = "G://bdb";
        long cacheSize = 100000;
        EnvironmentConfig envConfig = new EnvironmentConfig();
        //当设置为true时，说明若没有数据库的环境时，可以打开。否则就不能打开
        envConfig.setAllowCreate(true);
//        envConfig.setTransactional(true);
        envConfig.setSharedCache(true);
        envConfig.setLocking(false);
        envConfig.setCacheSize(cacheSize);
        //设置事务
        //envConfig.setTransactional(true);
        //当提交事务的时候是否把缓存中的内容同步到磁盘中去。true 表示不同步，也就是说不写磁盘
        //envConfig.setTxnNoSync(true);
        //当提交事务的时候，是否把缓冲的log写到磁盘上,true 表示不同步，也就是说不写磁盘
        //envConfig.setTxnWriteNoSync(true);
        try {
            env = new Environment(new File(path), envConfig);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    public BerkeleyDemo() {

    }

    /**
     * 构建berkeley数据库的开发环境
     *
     * @param path      配置Berkeley数据库的目录
     * @param cacheSize 配置缓冲的大小
     */
    public void setUp(String path, long cacheSize) {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        //当设置为true时，说明若没有数据库的环境时，可以打开。否则就不能打开
        envConfig.setAllowCreate(true);  //如果不存在，则可以重新建立
        envConfig.setTransactional(true);  // 支持事务
//        envConfig.setLocking(true);
        envConfig.setCacheSize(cacheSize);  //获取缓冲大小
        //设置事务
        //envConfig.setTransactional(true);
        //当提交事务的时候是否把缓存中的内容同步到磁盘中去。true 表示不同步，也就是说不写磁盘
        //envConfig.setTxnNoSync(true);
        //当提交事务的时候，是否把缓冲的log写到磁盘上,true 表示不同步，也就是说不写磁盘
        //envConfig.setTxnWriteNoSync(true);
        try {
            env = new Environment(new File(path), envConfig);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接berkeley数据库
     *
     * @param dbName
     */
    public void open(String dbName) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        //设置数据的是否可以创建的属性
        dbConfig.setAllowCreate(true);
        try {
            db = env.openDatabase(null, dbName, dbConfig);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭berkeley数据库
     */
    public void close() {
        try {
            if (db != null) {
                db.close();
            }
            if (env != null) {
                env.cleanLog();
                env.close();
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过key 得到数据
     *
     * @param key
     * @return
     * @throws Exception
     */
    public String get(String key) throws Exception {
        DatabaseEntry queryKey = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();
        queryKey.setData(key.getBytes("UTF-8"));
        OperationStatus status = db.get(null, queryKey, value, LockMode.DEFAULT);
        if (status == OperationStatus.SUCCESS) {
            return new String(value.getData(), "utf-8");
        }
        return null;
    }

    /**
     * Berkeley中保存数据
     *
     * @param key   Berkeley中的key值
     * @param value Berkeley中的value值
     * @return
     * @throws Exception
     */
    public boolean put(String key, String value) throws Exception {
        byte[] theKey = key.getBytes("UTF-8");
        byte[] theValue = value.getBytes("UTF-8");
        OperationStatus status = db.put(null, new DatabaseEntry(theKey), new DatabaseEntry(theValue));
        if (status == OperationStatus.SUCCESS) {
            return true;
        }
        return false;
    }

    /**
     * 通过key值删除对应的值
     *
     * @param key
     * @return
     * @throws Exception
     */
    public boolean del(String key) throws Exception {
        DatabaseEntry queryKey = new DatabaseEntry();
        queryKey.setData(key.getBytes("UTF-8"));
        OperationStatus status = db.delete(null, queryKey);
        if (status == OperationStatus.SUCCESS) {
            return true;
        }
        return false;
    }

    public Environment getEnv() {
        return env;
    }

    public Database getDb() {
        return db;
    }

    public static void main(String[] args) throws Exception {

        BerkeleyDemo mbdb = new BerkeleyDemo();

        //首先在本地创建文件夹bdb
        mbdb.setUp("G://bdb", 100000);
        mbdb.open("myDB");

        System.out.println(mbdb.getEnv().getConfig());

        // 保存数据
        System.out.println(" BerkeleyDB中保存数据。。。 ");
        for (int i = 0; i < 10; i++) {
            System.out.println("正在执行：" + i);
            String key = "myKeyB" + i;
            String value = "myValueB" + i;
            try {
                mbdb.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //查询数据
        try {
            System.out.println("查询到的数据为：" + mbdb.get("myKey2"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //删除数据
        try {
            if (mbdb.del("myKey2")) {
                System.out.println("删除数据成功");
            } else {
                System.out.println("删除数据失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //关闭数据库连接
        mbdb.close();
    }

}
