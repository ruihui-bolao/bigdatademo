import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/1/29 10:25
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   关于文件相关处理的工具类
 */
public class FileUtils {

    /**
     * 递归合并指定目录下的文件内容
     *
     * @param inputPath 输入文件路径
     * @return 关于文件内容的  Stringbuffer
     */
    public static StringBuffer combineFile(String inputPath) {
        File file = new File(inputPath);
        StringBuffer sb = new StringBuffer();
        if (file.exists()) {
            File[] files = file.listFiles();
            Reader reader = null;
            BufferedReader bf = null;
            try {
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        StringBuffer stringBuffer = combineFile(subFile.getPath());
                        sb.append(stringBuffer);
                    } else {
                        reader = new FileReader(subFile);
                        bf = new BufferedReader(reader);
                        String data = null;
                        while ((data = bf.readLine()) != null) {
                            sb.append(data).append("\n");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("输入文件不存在，请重新输入指定文件路径");
        }
        return sb;
    }

    /**
     * 保存文件
     *
     * @param stringBuffer 保存文件的StringBuffer
     * @param savaPath     保存文件路径
     */
    public static void savaFile(StringBuffer stringBuffer, String savaPath) {

        File newFile = new File(savaPath);
        try {
            if (newFile.exists())// 存在，则删除
                if (!newFile.delete())// 删除成功则创建
                {
                    System.err.println("删除文件" + newFile + "失败");
                }
            if (newFile.createNewFile()) {// 创建成功，则写入文件内容
                PrintWriter p = new PrintWriter(new FileOutputStream(newFile.getAbsolutePath()));
                p.write(stringBuffer.toString());
                p.close();
            } else {
                System.err.println("创建文件：" + newFile + "失败");
            }
            System.out.println("整合文件成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        String inputPath = "C:\\Users\\sssd\\Desktop\\huitemp";
        String savaPath = "C:\\Users\\sssd\\Desktop\\huitemp\\a.json";
        StringBuffer stringBuffer = FileUtils.combineFile(inputPath);
        System.out.println(stringBuffer.toString());
        FileUtils.savaFile(stringBuffer, savaPath);
    }
}
