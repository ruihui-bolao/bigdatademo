import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/14 15:08
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class ThreadSaveFile {

    private CopyOnWriteArrayList<String> DATA_LISTS = new CopyOnWriteArrayList<String>();

    protected String uploadPath = "C:\\Users\\sssd\\Desktop\\data";

    protected File file = null;

    private int a1 = 0;


    class AddValueThread extends Thread {
        @Override
        public void run() {
            int i = 1;
            int all_num = 0;
            while (i < 30000) {
                String name = Thread.currentThread().getName();
                String temp = name + "当前开始第当前开始第当前第当始第当前前开开始第第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第当前开始第" + i + "操作";
                System.out.println(temp);
                DATA_LISTS.add(temp);
                i++;
            }

            all_num = all_num + DATA_LISTS.size();
            System.out.println("@@@总共大小" + all_num);
        }
    }

    class DataSaveThread extends Thread {
        @Override
        public void run() {
            while (true) {
                String name = Thread.currentThread().getName();
                System.out.println("线程" + name + "线程运行中。。。");
                if (DATA_LISTS.size() >= 5) {
                    System.out.println("线程" + name + "开始执行操作！");
                    final DateTime dateTime = DateTime.now();

                    final String yyyyMMdd = dateTime.toString("yyyyMMdd");

                    final int hourOfDay = dateTime.getHourOfDay();

                    final String parent = uploadPath.endsWith(File.separator) ? uploadPath + yyyyMMdd : uploadPath + File.separator + yyyyMMdd;
                    mkdir(parent);
                    file = new File(parent, "WIFI-DATA" + ".json");

                    CopyOnWriteArrayList<String> data_lists = DATA_LISTS;
                    final String datas = StringUtils.join(data_lists, "\n");
                    if (StringUtils.isNotBlank(datas)) {
                        writeFile(file, datas);
                    }
                    a1 += data_lists.size();
                    DATA_LISTS.removeAll(data_lists);

                }
                System.out.println("###" + a1);
            }
        }
    }

    /**
     * 创建目录
     *
     * @param parent
     */
    private void mkdir(String parent) {
        final File dir = new File(parent);

        dir.mkdirs();
    }

    /**
     * 写数据到文件
     *
     * @param file
     * @param str
     */
    public void writeFile(File file, String str) {

        RandomAccessFile fout = null;
        FileChannel fcout = null;
        try {
            fout = new RandomAccessFile(file, "rw");
            fcout = fout.getChannel();              //打开文件通道
            FileLock flout = null;

            while (true) {
                try {
                    flout = fcout.tryLock();        //不断的请求锁，如果请求不到，等一秒再请求
                    break;
                } catch (Exception e) {
                    System.out.print("lock is exist ......");
                    Thread.sleep(1000);
                }
            }

            long filelength = fout.length();        //获取文件的长度
            fout.seek(filelength);                  //将文件的读写指针定位到文件的末尾
            fout.write((str + "\n").getBytes());    //将需要写入的内容写入文件
            flout.release();
            fcout.close();
            fout.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fcout != null) {
                try {
                    fcout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fcout = null;
                }
            }

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    fout = null;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadSaveFile threadSaveFile = new ThreadSaveFile();
        AddValueThread addValueThread = threadSaveFile.new AddValueThread();
        DataSaveThread dataSaveThread = threadSaveFile.new DataSaveThread();
        dataSaveThread.setName("保存");
        dataSaveThread.setDaemon(true);
        dataSaveThread.start();
        addValueThread.setName("添加");
        addValueThread.start();
    }
}
