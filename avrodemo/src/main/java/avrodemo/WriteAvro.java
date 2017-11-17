package avrodemo;

import com.sdyc.avro.demo.User;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/26 11:19
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  用java实现序列化，即写avro文件
 */
public class WriteAvro {
    public static void main(String[] args) throws Exception {
        //方式一
        User user1 = new User();
        user1.setName("zhangsan");
        user1.setFavoriteNumber(21);
        user1.setFavoriteColor(null);

        //方式二 使用构造函数
        User user2 = new User("Ben", 7, "red");

        //方式三，使用Build方式
        User user3 = User.newBuilder().setName("Charlie")
                .setFavoriteColor("blue")
                .setFavoriteNumber(null)
                .build();

        String path = "G:\\tmp\\user.avro"; // avro文件存放目录
        SpecificDatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(user1.getSchema(), new File(path));
        // 把生成的user对象写入到avro文件
        dataFileWriter.append(user1);
        dataFileWriter.append(user2);
        dataFileWriter.append(user3);
        dataFileWriter.close();
    }
}
