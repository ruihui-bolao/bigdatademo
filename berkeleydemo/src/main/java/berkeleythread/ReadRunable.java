package berkeleythread;

import berkeleythread.impl.BerkeleydbDaoSortedMapImpl;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/18 19:29
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class ReadRunable implements Runnable {
    private BerkeleydbDaoSortedMapImpl myBerkeley;
    private String key = "myKeyB2";

    public ReadRunable(BerkeleydbDaoSortedMapImpl berkeleyDemo) {
        this.myBerkeley = berkeleyDemo;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1500);
                System.out.println("查询到的数据为：" + myBerkeley.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
