package sdyc.ndmp.etl.loader.util;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class AvroUtilsTest {

    private Schema schema;

    @Test
    public void test() throws Exception{
        File file = new File("C:\\Users\\sssd\\Desktop\\hengqin\\AOMEN_FOOD_REVIEW.2017_10_26_13_57_54.avroc");
        schema = AvroUtils.parseSchema(new File("C:\\Users\\sssd\\Desktop\\hengqin\\TRAVEL_SCENIC_REVIEW.avro"));
        DataFileReader<GenericRecord> reader = new DataFileReader<GenericRecord>(
                file, new GenericDatumReader<GenericRecord>(schema));
        while (reader.hasNext()){
            GenericRecord record = reader.next();
            System.out.println(record);
        }
        reader.close();
    }
}