package com.sdyc.jise;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/26 16:18
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   读取 avro文件
 */
public class ReadAvrocFile {

    public static void main(String[] args) throws Exception {
        SpecificDatumReader<AomenFoodReview> userDatumReader = new SpecificDatumReader<AomenFoodReview>(AomenFoodReview.class);
        DataFileReader<AomenFoodReview> dataFileReader = new DataFileReader<AomenFoodReview>(new File("C:\\Users\\sssd\\Desktop\\hengqin\\AomenFoodReview.avroc"), userDatumReader);
        AomenFoodReview aomenFoodReview = null;
        while (dataFileReader.hasNext()){
            try {
                aomenFoodReview = dataFileReader.next();
                System.out.println(aomenFoodReview);
            } catch (Exception e) {
                System.out.println(dataFileReader.next());
            }
        }
    }
}
