package com.sdyc.temp.test;

import org.rocksdb.*;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/2 18:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class RocksdbTest {


    static{
        try {
            RocksDB.loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }


    /**
     *
     * @throws RocksDBException
     */
    public void run() throws RocksDBException {

        Options options = new Options();
        options.setAllowFAllocate(true);
        options.setArenaBlockSize(1024);
        options.setCreateIfMissing(true);
        options.setDbWriteBufferSize(1024);
        options.setUseFsync(true);

        RocksDB rocksDB = RocksDB.open("E:/test");

        rocksDB.put("test".getBytes(), "value".getBytes());
        rocksDB.flush(new FlushOptions());

        rocksDB.close();


        rocksDB = RocksDB.open("E:/test");
        RocksIterator rocksIterator = rocksDB.newIterator();

        rocksIterator.seekToFirst();
        while(rocksIterator.isValid()){
            System.out.println(new String(rocksIterator.key()) + ": " + new String(rocksIterator.value()));
            rocksIterator.next();
        }

        rocksIterator.close();

        rocksDB.close();
    }


    public static void main(String[] args) throws RocksDBException {


        RocksdbTest rocksdbTest = new RocksdbTest();

        rocksdbTest.run();

    }
}
