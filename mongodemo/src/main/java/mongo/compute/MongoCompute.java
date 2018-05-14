package mongo.compute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/11 10:33
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  主要是通过js实现mongo中的map-reduce操作
 */
public class MongoCompute implements Serializable {

    /**
     * mongo的全部数据库
     */
    private transient List<String> mongoList;

    /**
     * mongo server
     */
    private transient String mongServer;

    /**
     * 配置文件
     */
    private transient Configuration conf;

    /**
     * 构造器初始化参数
     *
     * @param parmas
     */
    public MongoCompute(Map parmas) {
        List mongoList = (List) parmas.get("mongoDbList");
        if (mongoList == null || mongoList.isEmpty()) {
            throw new IllegalArgumentException("mongo 的数据库不能为空！");
        } else {
            this.mongoList = mongoList;
        }

        String mongoServer = (String) parmas.get("mongoServer");
        if (StringUtils.isBlank(mongoServer)) {
            throw new IllegalArgumentException("mongo 的server不能为空！");
        } else {
            this.mongServer = mongoServer;
        }

        this.conf = new Configuration();
    }

    public void run() {
        // 初始化sparkconf
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("TestMongo").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // 获取mongo的数据库和mongoServer
        final String DBindex = mongoList.get(1);
        final String mongoServerUrl = mongServer.endsWith("/") ? mongServer + "WifiMac." + DBindex : mongServer + "/WifiMac." + DBindex;

        // 初始化配置文件
        conf.set(MongoConfigUtil.INPUT_URI, mongoServerUrl);
        conf.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");

        // 其中conf 定义的是配置文件，MongoInputFormat定义的是mongo的输入文件，后两个参数定义的是输出文件，Object定义的是mongo中objectId，BSONObject定义的是mongo文档
        Map<String, List<String>> resMap = sc.newAPIHadoopRDD(conf, MongoInputFormat.class, Object.class, BSONObject.class)
                // BSONObject为mongo 数据
                .mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<Object, BSONObject>>, String, String>() {
                    @Override
                    public Iterator<Tuple2<String, String>> call(Iterator<Tuple2<Object, BSONObject>> tuple2Iterator) throws Exception {
                        LinkedList<Tuple2<String, String>> resLists = new LinkedList<Tuple2<String, String>>();
                        while (tuple2Iterator.hasNext()) {
                            Tuple2<Object, BSONObject> next = tuple2Iterator.next();
                            // 获取的是mongo中的数据
                            final BSONObject bsonObject = next._2();
                            final JSONObject jsonObject = (JSONObject) JSON.toJSON(bsonObject.toMap());
                            String id = jsonObject.getString("_id");
                            String[] split = id.split("#");
                            if (split.length == 2) {
                                // 获取wifimac地址
                                final String clientMac = split[0];
                                // 获取景点id
                                final String site = split[1];
                                resLists.add(new Tuple2<String, String>(site, clientMac));
                            }
                        }
                        return resLists.iterator();
                    }
                }).groupByKey().mapToPair(new PairFunction<Tuple2<String, Iterable<String>>, String, List<String>>() {
                    @Override
                    public Tuple2<String, List<String>> call(Tuple2<String, Iterable<String>> iterableTuple2) throws Exception {
                        final String site = iterableTuple2._1();

                        final Iterator<String> iterator = iterableTuple2._2().iterator();
                        LinkedList<String> sets = new LinkedList<String>();
                        // 取出手机mac地址，并将其加上":"
                        while (iterator.hasNext()) {
                            sets.add(String.valueOf(iterator.next()));
                        }
                        return new Tuple2<String, List<String>>(site, sets);
                    }
                }).collectAsMap();
        System.out.println("获取数据的大小为:" + resMap.size());
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        BeanFactory factory = context;
        Map parms = (Map) factory.getBean("parms");
        MongoCompute mongoCompute = new MongoCompute(parms);
        mongoCompute.run();
    }

}
