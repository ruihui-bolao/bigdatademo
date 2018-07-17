package spark.es.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.elasticsearch.hadoop.cfg.ConfigurationOptions;
import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.mr.WritableArrayWritable;
import org.elasticsearch.hadoop.util.WritableUtils;
import scala.Tuple2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/7/14 9:57
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   spark es utils
 */
public class EsUtils implements Serializable {

    /**
     * 日志
     */
    protected static Log LOG = LogFactory.getLog(EsUtils.class);

    /**
     * es map dataType => esIndex
     */
    public static final HashMap<String, String> ES_INDEX_TYPE_MAP = new HashMap<String, String>(3);


    static {
        ES_INDEX_TYPE_MAP.put("weibo_user", "hui_test_user/user_test");
        ES_INDEX_TYPE_MAP.put("weibo_post", "hui_test_post/post_test");
    }

    /**
     * spark write es conf
     *
     * @param conf    conf
     * @param esId    es 主键
     * @param esIndex es index
     * @param esNodes es 集群节点
     * @return
     */
    public static Configuration buildWriteConf(Configuration conf, String esId, String esIndex, String esNodes) {
        conf.set("es.index.auto.create", "true");
        // 指定es index
        conf.set("es.resource", esIndex);
        // 制定es 主键
        conf.set("es.mapping.id", esId);
        // 当前task 的 task id
        conf.set("mapred.tip.id", "task_201707121733_0003_m_000005");
        // 指定es节点
        conf.set("es.nodes", esNodes);
        return conf;
    }

    /**
     * spark write to es
     *
     * @param list
     * @param conf
     */
    public static void sparkWriteEs(List list, Configuration conf, JavaSparkContext sc) {
        JavaRDD<Object> javaRDD = sc.parallelize(list);
        javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<Object>, NullWritable, MapWritable>() {
            @Override
            public Iterable<Tuple2<NullWritable, MapWritable>> call(Iterator<Object> objectIterator) throws Exception {
                LinkedList<Tuple2<NullWritable, MapWritable>> resList = new LinkedList<Tuple2<NullWritable, MapWritable>>();
                while (objectIterator.hasNext()) {
                    MapWritable writable = new MapWritable();
                    final Object temp = objectIterator.next();
                    bean2Spark(writable, temp);
                    resList.add(new Tuple2<NullWritable, MapWritable>(null, writable));
                }
                return resList;
            }
        }).saveAsNewAPIHadoopFile("", NullWritable.class, MapWritable.class, EsOutputFormat.class, conf);
    }

    /**
     * object bean to spark es writable
     *
     * @param writable spark 写入 es 的数据格式 writable
     * @param temp
     */
    private static void bean2Spark(MapWritable writable, Object temp) {

    }

    /**
     * build es query conf（By time）
     *
     * @param conf      hadoop conf
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param esIndexs  esindex
     * @param esNodes   esnode
     * @param dataType  数据类型
     * @return
     */
    public static Configuration buildQueryByTimeConf(Configuration conf, String startTime, String endTime, String esIndexs, String esNodes, String dataType) {
        Configuration config = conf;
        //指定读取的索引名称
        config.set(ConfigurationOptions.ES_RESOURCE, esIndexs);
        //正式环境指定es节点
        config.set(ConfigurationOptions.ES_NODES, esNodes);

        config.set(ConfigurationOptions.ES_READ_FIELD_EMPTY_AS_NULL, "false");

        config.set(ConfigurationOptions.ES_QUERY, buildQueryByTimeStr(dataType, startTime, endTime));

        return config;
    }


    /**
     * build es query string (by time)
     *
     * @param dataType  数据类型
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    public static String buildQueryByTimeStr(String dataType, String startTime, String endTime) {

        ImmutableMap<String, String> tempmap = ImmutableMap.of("gt", startTime, "lte", endTime);
        HashMap<String, Object> conditionMap = new HashMap<String, Object>(1);
        if (StringUtils.equalsIgnoreCase(dataType, "post")) {
            conditionMap.put("query", ImmutableMap.of("range", ImmutableMap.of("DW_CREATED_AT", tempmap)));
        } else if (StringUtils.equalsIgnoreCase(dataType, "user")) {
            conditionMap.put("query", ImmutableMap.of("range", ImmutableMap.of("DW_UPDATED_AT", tempmap)));
        } else {
            LOG.info("未知的数据类型！！！");
        }
        String condition = JSON.toJSONString(conditionMap);
        return condition;
    }

    /**
     * query data from es
     *
     * @param conf             hadoop conf
     * @param javaSparkContext sparkcontext
     * @param startTime        开始时间
     * @param endTime          结束时间
     * @param esIndex          esindes
     * @param esNodes          esnodes
     * @param dataType         数据类型
     * @return
     */
    public static JavaRDD<JSONObject> queryDataByTime(Configuration conf, JavaSparkContext javaSparkContext, String startTime, String endTime, String esIndex, String esNodes, String dataType) {
        Configuration queryByTimeConf = buildQueryByTimeConf(conf, startTime, endTime, esIndex, esNodes, dataType);
        JavaRDD<JSONObject> javaRDD = javaSparkContext.newAPIHadoopRDD(queryByTimeConf, EsInputFormat.class, NullWritable.class, MapWritable.class)
                .mapPartitions(new FlatMapFunction<Iterator<Tuple2<NullWritable, MapWritable>>, JSONObject>() {
                    @Override
                    public Iterable<JSONObject> call(Iterator<Tuple2<NullWritable, MapWritable>> iterator) throws Exception {
                        LinkedList<JSONObject> resList = new LinkedList<JSONObject>();
                        while (iterator.hasNext()) {
                            final Tuple2<NullWritable, MapWritable> next = iterator.next();
                            final MapWritable mapWritable = next._2;
                            JSONObject jsonObject = new JSONObject();
                            final Set<Map.Entry<Writable, Writable>> entries = mapWritable.entrySet();
                            for (Map.Entry<Writable, Writable> entry : entries) {
                                final Writable key = entry.getKey();
                                final Writable value = entry.getValue();
                                if (value instanceof WritableArrayWritable) {
                                    WritableArrayWritable var6 = (WritableArrayWritable) value;
                                    jsonObject.put(key.toString(), var6.toStrings());
                                } else {
                                    jsonObject.put(key.toString(), value.toString());
                                }
                            }
                            resList.add(jsonObject);
                        }
                        return resList;
                    }
                });
        return javaRDD;
    }

    /**
     * build es update conf
     *
     * @param conf    hadoop conf
     * @param esId    es 主键
     * @param esIndex esIndex
     * @param esNodes esnode
     * @return
     */
    public static Configuration buildUpdateConf(Configuration conf, String esId, String esIndex, String esNodes) {
        conf.set("es.index.auto.create", "true");
        conf.set("es.resource", esIndex);
        conf.set("es.mapping.id", esId);
        conf.set("es.write.operation", "upsert");
        conf.set("mapred.tip.id", "task_201807121733_0003_m_000005");
        conf.set("es.nodes", esNodes);
        return conf;
    }

    /**
     * es update postdata
     *
     * @param conf        hadoop conf
     * @param esId        es 主键
     * @param esIndex     es index
     * @param esNodes     es nodex
     * @param sc          javasparkcontext
     * @param javaPairRDD javaRDD 数据
     */
    public static void updatePostData(Configuration conf, String esId, String esIndex, String esNodes, JavaSparkContext sc, JavaPairRDD<String, JSONObject> javaPairRDD) {
        Configuration configuration = buildUpdateConf(conf, esId, esIndex, esNodes);
        javaPairRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<Tuple2<String, JSONObject>>, NullWritable, MapWritable>() {
            @Override
            public Iterable<Tuple2<NullWritable, MapWritable>> call(Iterator<Tuple2<String, JSONObject>> iterator) throws Exception {
                LinkedList<Tuple2<NullWritable, MapWritable>> resList = new LinkedList<Tuple2<NullWritable, MapWritable>>();
                while (iterator.hasNext()) {
                    Tuple2<String, JSONObject> next = iterator.next();
                    JSONObject jsonObject = next._2;
                    Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
                    final MapWritable writableMap = new MapWritable();
                    for (Map.Entry<String, Object> entry : entries) {
                        final Object entryValue = entry.getValue();
                        if (entryValue instanceof BigDecimal) {
                            BigDecimal a = new BigDecimal(((BigDecimal) entryValue).doubleValue());
                            writableMap.put(new Text(entry.getKey()), WritableUtils.toWritable(a.doubleValue()));
                        } else {
                            writableMap.put(new Text(entry.getKey()), WritableUtils.toWritable(entryValue));
                        }
                    }
                    resList.add(new Tuple2<NullWritable, MapWritable>(null, writableMap));
                }
                return resList;
            }
        }).saveAsNewAPIHadoopFile("", NullWritable.class, MapWritable.class, EsOutputFormat.class, configuration);
    }

    public static SparkConf buildCountDataFromEsByKey(SparkConf conf, String esIndexs, String esNodes) {
        SparkConf config = conf;
        //指定读取的索引名称
        config.set(ConfigurationOptions.ES_RESOURCE, esIndexs);
        //正式环境指定es节点
        config.set(ConfigurationOptions.ES_NODES, esNodes);
        config.set(ConfigurationOptions.ES_READ_FIELD_EMPTY_AS_NULL, "false");
        return config;
    }


    public static HashMap<String, Long> countDataFromEsByKey(SparkConf conf, String esIndex, String esNodes, List<String> keys) {
        SparkConf newConf = buildCountDataFromEsByKey(conf, esIndex, esNodes);
        JavaSparkContext javaSparkContext = new JavaSparkContext(newConf);
        SQLContext sql = new SQLContext(javaSparkContext);
        sql.sql("CREATE TEMPORARY TABLE postTab USING org.elasticsearch.spark.sql OPTIONS (resource '" + esIndex + "')");

        HashMap<String, Long> resMap = new HashMap<String, Long>();
        for (String key : keys) {
            String tempsql = "select * from postTab where USER_ID = " + key;
            DataFrame dataFrame = sql.sql(tempsql);
            resMap.put(key, dataFrame.count());
        }
        return resMap;
    }

}
