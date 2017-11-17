package berkeleythread;


import berkeleythread.impl.BerkeleydbDaoSortedMapImpl;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/18 19:10
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class WriteRunable implements Runnable {

    private BerkeleydbDaoSortedMapImpl myBerkeley;
    private String key;
    private String value;

    public WriteRunable(BerkeleydbDaoSortedMapImpl berkeleyDemo) {
        this.myBerkeley = berkeleyDemo;
    }

    @Override
    public void run() {
        System.out.println(" BerkeleyDB中保存数据。。。 ");
        for (int i = 0; i < 1000; i++) {
            System.out.println("正在执行：" + i);
            key = "myKeyB" + i;
            value = "myValueB" + i;
            try {
                myBerkeley.save(key, value);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
