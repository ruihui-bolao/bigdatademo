package berkeleythread.impl;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.*;
import berkeleythread.BerkeleydbDao;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/18 14:17
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  使用StoredMap实现
 */
public class BerkeleydbDaoSortedMapImpl<T> implements BerkeleydbDao<T> {

    Environment env = null;
    private Database database = null;
    private StoredMap<String, T> storedMap = null;
    private Class<T> persistentClass = null;
    private Transaction txn = null;
    EntryBinding<String> keyBinding = null;
    SerialBinding<T> valueBinding = null;

    public BerkeleydbDaoSortedMapImpl(Class<T> persistentClass){
        this.persistentClass = persistentClass;
    }


    @Override
    public void openConnection(String filePath, String databaseName) {
        File file = new File(filePath);
        EnvironmentConfig envConfig = new EnvironmentConfig();
        //不存在时，允许创建
        envConfig.setAllowCreate(true);
        //设置事务属性
        envConfig.setTransactional(true);
        env = new Environment(file, envConfig);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        //当不存在数据库的时候允许创建一个新的数据
        databaseConfig.setAllowCreate(true);
        //设置事务属性
        databaseConfig.setTransactional(true);
        // 允许数据库存在重复的数据
//        databaseConfig.setSortedDuplicates(true);
        database = env.openDatabase(null, databaseName, databaseConfig);
        StoredClassCatalog catalog = new StoredClassCatalog(database);
        keyBinding = new SerialBinding<String>(catalog, String.class);
        valueBinding = new SerialBinding<T>(catalog, persistentClass);
        storedMap = new StoredMap<String, T>(database, keyBinding, valueBinding, true);
    }

    @Override
    public void closeConnection() {
        if(database != null){
            database.close();
            if(env != null){
                env.cleanLog();
                env.close();
            }
        }
    }

    @Override
    public void save(String name, T t) {
        storedMap.put(name, t);
    }

    @Override
    public void delete(String name) {
        storedMap.remove(name);
    }

    @Override
    public void update(String name, T t) {
        save(name, t);
    }

    @Override
    public T get(String name) {
        return storedMap.get(name);
    }

    public Environment getEnv() {
        return env;
    }

    public Database getDatabase() {
        return database;
    }
    public Transaction getTxn() {
        return txn;
    }
}
