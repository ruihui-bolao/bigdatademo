package avrodemo;

import com.sdyc.avro.demo.User;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/26 13:42
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class ReadAvro {
    public static void main(String[] args) throws Exception{
        SpecificDatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(new File("G:\\tmp\\user.avro"), userDatumReader);
        User user = null;
        while (dataFileReader.hasNext()) {
            user = dataFileReader.next();
            System.out.println(user);
        }
    }
}
