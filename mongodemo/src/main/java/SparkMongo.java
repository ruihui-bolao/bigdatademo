
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import scala.Tuple2;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sssd on 2017/8/25.
 *
 *  利用spark 的 mapreduce连接 mongo
 */
public class SparkMongo {

    public static void run() {

        //Spark 的相关配置
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("MongodbSpark")
                .set("spark.mongodb.input.uri", "mongodb://192.168.1.115:8888/data720.test_ay")
                .set("spark.mongodb.output.uri", "mongodb:192.168.1.115:8888/data720.test_ay");

        //配置sparkContext
        JavaSparkContext sparkContext = new JavaSparkContext(conf);

        //设置mongodb的读配置
        HashMap<String, String> readOverrides = new HashMap<String, String>();
        //设置读取的collection
        readOverrides.put("collection", "test_ax");
        ReadConfig readConfig = ReadConfig.create(sparkContext).withOptions(readOverrides);

        /*//mongodb 单条件查询（单条件）
        JSONObject searcQuery = new JSONObject();
        JSONObject condList = new JSONObject();
        searcQuery.put("postUrn", "829343946f16f2a928afcb6d19a8ec54");
        Query query = new Query();
        query.addCriteria(Criteria.where("postUrn").all("829343946f16f2a928afcb6d19a8ec54").and("_id").all("829343946f16f2a928afcb6d19a8ec54"));
        condList.put("$match",JSON.parse(query.getQueryO    bject().toString()));
        JavaMongoRDD<Document> customRdd = MongoSpark.load(sparkContext, readConfig).withPipeline(
                Collections.singletonList(Document.parse(condList.toJSONString()))
        );
        System.out.println("查询的结果数目为： " + customRdd.count() );
        System.out.println("查询的结果显示为： " + customRdd.first().toJson());*/

        //mongodb 单条件查询(一段时间查询)
        Date endDate = new Date();
        // 获取前一天的时间
        Calendar calendar = Calendar.getInstance();   // 得到Calendar的一个实例。
        calendar.setTime(endDate);                        //设置时间为当前时间
        calendar.add(Calendar.DATE, -10);              //获取天数并减1
        Date startDate = calendar.getTime();                //转换为时间
        JSONObject condList = new JSONObject();
        Query query = new Query();
        query.addCriteria(Criteria.where("dwCreatedAt").gte(startDate).lte(endDate));
        condList.put("$match", JSON.parse(query.getQueryObject().toString()));
        JavaMongoRDD<Document> customRdd = MongoSpark.load(sparkContext, readConfig).withPipeline(
                Collections.singletonList(Document.parse(condList.toJSONString()))
        );

        //map
        JavaPairRDD<Integer, Integer> result = customRdd.mapToPair(
                new PairFunction<Document, Integer, Integer>() {
                    public Tuple2<Integer, Integer> call(Document agr0) throws Exception {
                        Date date = (Date) agr0.get("dwCreatedAt");
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(date);
                        int day = instance.get(Calendar.DATE);
                        return new Tuple2<Integer, Integer>(Integer.valueOf(day), Integer.valueOf(1));
                    }
                }
        ).cache();
        // reduce
        result = result.reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    public Integer call(Integer arg0, Integer arg1) throws Exception {
                        return arg0 + arg1;
                    }
                }
        );

        // map
        JavaRDD<Document> save = result.map(
                new Function<Tuple2<Integer, Integer>, Document>() {
                    public Document call(Tuple2<Integer, Integer> arg0) throws Exception {
                        return Document.parse("{date:\"" + arg0._1 + "\",num:" + arg0._2 + "}");
                    }
                }
        );


        System.out.println(save.count());
        System.out.println(save.collect());

        sparkContext.close();
    }

    public static void main(String[] args) {
        SparkMongo.run();
    }

}
