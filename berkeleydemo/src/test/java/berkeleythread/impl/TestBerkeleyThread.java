package berkeleythread.impl;

import berkeleythread.ReadRunable;
import berkeleythread.WriteRunable;

public class TestBerkeleyThread {

    public static void main(String[] args) throws Exception{
        BerkeleydbDaoSortedMapImpl<String> mybdb = new BerkeleydbDaoSortedMapImpl<String>(String.class);
        // 创建连接
        mybdb.openConnection("G://bdb", "myDB");

        WriteRunable writeRunable = new WriteRunable(mybdb);
        ReadRunable readRunable = new ReadRunable(mybdb);
        Thread thread1 = new Thread(writeRunable);
        Thread thread2 = new Thread(readRunable);
        thread1.start();
        thread2.start();

//        mybdb.closeConnection();
/*        // 保存数据
        mybdb.save("名字", "张三");*/

       /* for (int i = 0; i<10000; i++){
            Thread.sleep(1000);
            System.out.println("正在执行：" + i);
            String key = "myKey" + i;
            String value = "myValue" + i;
            mybdb.save(key, value);
        }*/


/*        // 查
        String value = mybdb.get("myKeyB2");
        System.out.println("查到数据为：" + value);*/

     /*   // 修改
        mybdb.update("名字", "李四");
        System.out.println("查到数据为：" + mybdb.get("名字"));
        // 删除
        mybdb.delete("名字");
        System.out.println("查到数据为：" + mybdb.get("名字"));
        mybdb.closeConnection();*/
    }
}