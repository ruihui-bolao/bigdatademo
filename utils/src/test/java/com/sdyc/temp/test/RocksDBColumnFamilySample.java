package com.sdyc.temp.test;
import org.rocksdb.*;

import java.util.ArrayList;
import java.util.List;

public class RocksDBColumnFamilySample {


    private final static String db_path = "F:/dbtest1";
    private static Options options;
    private static RocksDB db;

    private static DBOptions dboptions = new DBOptions();

    private static List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<ColumnFamilyHandle>();

    static {
        RocksDB.loadLibrary();
    }


    //创建列簇
    public static void createColumnFamily(List<String> columnNames) throws RocksDBException{
        options = new Options().setCreateIfMissing(true);
        db = RocksDB.open(options, db_path);

        for(String columnName : columnNames) {
            System.out.println("createColumnFamily " + columnName);
            db.createColumnFamily(new ColumnFamilyDescriptor(columnName.getBytes(), new ColumnFamilyOptions()));
            System.out.println("createColumnFamily " + columnName + " over");
        }

        db.close();
    }


    //获取所有列簇的handle
    public static void getAllColumnFamilyHandles(List<String> columnsNameList) throws RocksDBException{

        // open DB with two column families
        List<ColumnFamilyDescriptor> columnFamilyDescriptors =  new ArrayList<ColumnFamilyDescriptor>();

        // have to open default column family
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor( RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));

        for(String columnName : columnsNameList) {
            // open the new one, too
            columnFamilyDescriptors.add(new ColumnFamilyDescriptor(columnName.getBytes(), new ColumnFamilyOptions()));
        }

        db = RocksDB.open(dboptions, db_path, columnFamilyDescriptors, columnFamilyHandles);
    }



    public static void writeDataToAllFamillys() throws RocksDBException {
        try {

            int size = columnFamilyHandles.size();
            System.out.println("column family handles list size is " + size);

            String key;
            String value;

            for(int i = 0; i < size; i++) {
                key = "key-" + i;
                value = "value-" + i;

                // put and get from non-default column family
                db.put(columnFamilyHandles.get(i), new WriteOptions(), key.getBytes(), value.getBytes());
            }

        } finally {
            for (final ColumnFamilyHandle handle : columnFamilyHandles) {
                handle.close();
            }
        }
    }

    public static void readDataFromAllFamillys() throws RocksDBException{
        int size = columnFamilyHandles.size();

        String key;

        for(int i = 0;i<size;i++) {
            key = "key-" + i;

            // put and get from non-default column family   new ReadOptions(),
            byte[] bytes = db.get(columnFamilyHandles.get(i),key.getBytes());

            System.out.println("the columnFamily is " + size + "the key is " + key + ",the value is " + new String(bytes));
        }
    }


    public static void automicWrite() throws RocksDBException {
       /* // atomic write
        try (final WriteBatch wb = new WriteBatch()) {
            wb.put(columnFamilyHandles.get(0), "key2".getBytes(), "value2".getBytes());
            wb.put(columnFamilyHandles.get(1), "key3".getBytes(), "value3".getBytes());
            wb.remove(columnFamilyHandles.get(0), "key".getBytes());
            db.write(new WriteOptions(), wb);
        }*/

        // drop column family
        db.dropColumnFamily(columnFamilyHandles.get(1));
    }




    public static void main(final String[] args) throws RocksDBException {


        ArrayList<String> list = new ArrayList<String>();

        list.add("column1");
        list.add("column2");
        list.add("column3");

        createColumnFamily(list);

        getAllColumnFamilyHandles(list);

        writeDataToAllFamillys();
        readDataFromAllFamillys();

    }
}