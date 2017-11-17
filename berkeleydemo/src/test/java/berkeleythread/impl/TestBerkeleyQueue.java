package berkeleythread.impl;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/20 9:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TestBerkeleyQueue {

    @Test
    public void test() {
        BerkeleydbDaoSortedMapImpl<String> mybdb = new BerkeleydbDaoSortedMapImpl<String>(String.class);
        mybdb.openConnection("G://bdb", "myDB1");
        mybdb.save("name", "zhang");
        mybdb.save("age", "12");
        mybdb.save("sex", "man");
        Cursor cursor = null;
        ArrayList<String> resList = new ArrayList<String>();
        try {
            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();
            cursor = mybdb.getDatabase().openCursor(null, null);
            if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                String theKey = new String(foundKey.getData(), "UTF-8");
                String theData = new String(foundData.getData(), "UTF-8");
                System.out.println("Key | Data : " + theKey + " | " +
                        theData + "");
                while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                    theKey = new String(foundKey.getData(), "UTF-8");
                    theData = new String(foundData.getData(), "UTF-8");
                    System.out.println("Key | Data : " + theKey + " | " +
                            theData + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            mybdb.closeConnection();
        }
    }
}
