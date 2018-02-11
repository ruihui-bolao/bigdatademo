package com.sdyc.temp.test;

import org.rocksdb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * User:        Ryan
 * Date:        2018/2/2
 * Email:       liuwei412552703@163.com
 * Version      V1.0
 * Discription:
 */
public class RocksDBBasicTest {

    // a static method that loads the RocksDB C++ library.
    static {
        RocksDB.loadLibrary();
    }

    private Options options;

    private RocksDB db;

    private final static String dbPath = "F:/dbtest/";

    private ReadOptions readOptions = new ReadOptions().setFillCache(false);

    private WriteOptions writeOptions = new WriteOptions();

    //构造函数
    public RocksDBBasicTest() throws RocksDBException {
        // the Options class contains a set of configurable DB options that determines the behaviour of the database.
        options = new Options().setCreateIfMissing(true);
        db = RocksDB.open(options, dbPath);
    }

    //清空数据库中数据
    public void clearDB() throws RocksDBException {
        db.destroyDB(dbPath, options);
    }

    /**
     * 想库中写入数据
     *
     * @param numbers     写入数据的条数
     * @param keyPrefix   键的前缀
     * @param valuePrefix 值的前缀
     * @throws RocksDBException
     */
    public void writeData(long numbers, String keyPrefix, String valuePrefix) throws RocksDBException {

        String key;
        String value;

        int i = 1;
        long beginMillis = System.currentTimeMillis();
        long cost;

        while (i <= numbers) {
            key = keyPrefix + i;
            value = valuePrefix + i;

            //inserting an entry
            db.put(key.getBytes(), value.getBytes());

            i++;
            cost = (System.currentTimeMillis() - beginMillis) / 1000;
            System.out.println("第" + i + "次写入,花费时间：" + Long.toString(cost));
        }
        System.out.println("写入完毕");
    }


    /**
     * 根据键前缀，一条一条读取，读取readNumbers条数据
     *
     * @param keyPrefix   键前缀
     * @param readNumbers 需要读取数据的条数
     * @throws Exception
     */
    public void readData(String keyPrefix, int readNumbers) throws Exception {

        long beginMillis = System.currentTimeMillis();
        int i = 1;
        String key;
        long cost;
        while (i <= readNumbers) {
            key = "k-" + i;

            System.out.println("key is " + key + ",value is " + new String(db.get(key.getBytes())));
            i++;
            cost = (System.currentTimeMillis() - beginMillis) / 1000;
            System.out.println("第" + i + "次读取,花费时间：" + Long.toString(cost));
        }
    }


    /**
     * 给定一些键，一次获取它们对应的值
     *
     * @param keylist 获取某些键的值的键list
     * @throws RocksDBException
     */
    public void multigetData(List<byte[]> keylist) throws RocksDBException {
        Map<byte[], byte[]> map = db.multiGet(keylist);

        for (byte[] key : map.keySet()) {
            byte[] value = db.get(key);

            System.out.println("the key is " + new String(key) + ",the value is " + new String(value));
        }
    }

    /**
     * 使用Iterator批量读取具有相同键前缀的数据
     *
     * @param keyPrefix 键前缀
     * @throws Exception
     */
    public void readData(String keyPrefix) throws Exception {

        //在数据库内容上返回一个迭代器，newIterator（）的结果最初是无效的，调用者在使用它之前必须调用迭代器上的Seek方法之一
        RocksIterator rocksIterator = db.newIterator();

        //查找具有相同前缀的key
        rocksIterator.seek(keyPrefix.getBytes());

        while (rocksIterator.isValid()) {
            byte[] key = rocksIterator.key();
            byte[] value = rocksIterator.value();

            System.out.println("key is " + new String(key) + ",value is " + new String(value));
            rocksIterator.next();
        }
    }


    /**
     * 迭代所有的键值对
     */
    public void readAllDatas() {
        RocksIterator rocksIterator = db.newIterator();
        rocksIterator.seekToFirst();


        //输出第一个键值对
        if (rocksIterator.isValid()) {
            System.out.println("第1次迭代" + "key is " + new String(rocksIterator.key()) + ",value is " + new String(rocksIterator.value()));
        }
        rocksIterator.next();

        int i = 2;
        while (rocksIterator.isValid()) {
            System.out.println("第" + i + "次迭代，" + "key is " + new String(rocksIterator.key()) + ",value is " + new String(rocksIterator.value()));
            i++;
            rocksIterator.next();
        }

    }


    /**
     * 获取rocksdb中默认列簇存储的键值对的估计数量
     *
     * @return
     * @throws RocksDBException
     */
    public long getCounts() throws RocksDBException {
        long keysNumber = db.getLongProperty("rocksdb.estimate-num-keys");
        System.out.println(keysNumber);

        return keysNumber;
    }


    /**
     * 操作列簇
     *
     * @param columnName
     */
    public static void operateColumnFamily(String columnName) throws RocksDBException {

//        //创建列簇
//        ColumnFamilyHandle columnFamilyHandle = db.createColumnFamily(
//                new ColumnFamilyDescriptor("columnName".getBytes(), new ColumnFamilyOptions()));

        // open DB with two column families
        final List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<ColumnFamilyDescriptor>();

        // have to open default column family
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
        // open the new one, too
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(columnName.getBytes(), new ColumnFamilyOptions()));


        List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<ColumnFamilyHandle>();
        DBOptions options = new DBOptions();


        String anotherDB_path = "F:/RocksDB/";
        //打开一个新的数据库
        RocksDB db = RocksDB.open(options, anotherDB_path, columnFamilyDescriptors, columnFamilyHandles);


        // put and get from non-default column family
        db.put(columnFamilyHandles.get(0), new WriteOptions(), "key".getBytes(), "value".getBytes());

        // atomic write
        WriteBatch wb = new WriteBatch();
        wb.put(columnFamilyHandles.get(0), "key2".getBytes(), "value2".getBytes());
        wb.put(columnFamilyHandles.get(1), "key3".getBytes(), "value3".getBytes());
        wb.remove(columnFamilyHandles.get(0), "key".getBytes());
        db.write(new WriteOptions(), wb);


        // drop column family
        db.dropColumnFamily(columnFamilyHandles.get(1));
    }


    public static void main(String[] args) throws Exception {


        RocksDBBasicTest rocksDBBasicTest = new RocksDBBasicTest();
        rocksDBBasicTest.writeData(100000, "key-", "Rocks DB test");


        Thread.sleep(10000);
        rocksDBBasicTest.readAllDatas();

//        ArrayList<byte[]> list = new ArrayList<>();
//        list.add("k-1".getBytes());
//        list.add("k-11".getBytes());
//        list.add("k-123".getBytes());
//        rocksDBBasicTest.multigetData(list);


    }
}