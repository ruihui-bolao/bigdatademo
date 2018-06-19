package spark.es.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
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
 * <pre>
 * Created with IntelliJ IDEA.
 * User: ruihui
 * Date: 2017/7/19
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 * </pre>
 *
 * @author ruihui
 */

/**
 * spark - es 的工具类
 */
public class ESUtils implements Serializable {
    /**
     * 日志文件
     */
    protected static Log LOG = LogFactory.getLog(ESUtils.class);

    /**
     * 存放es index-type 对应的map
     */
    public static final HashMap<String, String> ES_INDEX_TYPE_MAP = new HashMap<String, String>(3);

    /**
     * AND(与)关键词
     */
    public static final String AND_KEYS = "AND_KEYS";

    /**
     * OR(或)关键词
     */
    public static final String OR_KEYS = "OR_KEYS";

    /**
     * NOT(不包含关键词)
     */
    public static final String NOT_KEYS = "NOT_KEYS";

    /**
     *  es index/type map
     */
    static {
        ES_INDEX_TYPE_MAP.put("WQ_MOVIES", "wzx_movies/movies");
        ES_INDEX_TYPE_MAP.put("WQ_MUSIC", "wzx_music/music");
        ES_INDEX_TYPE_MAP.put("WQ_FICTION", "wzx_fiction/fiction");
        ES_INDEX_TYPE_MAP.put("WQ_CARICATURE", "wzx_caricature/caricature");
        ES_INDEX_TYPE_MAP.put("WQ_APP", "wzx_app/app");
        ES_INDEX_TYPE_MAP.put("WQ_SOFTWARE", "wzx_software/software");
        ES_INDEX_TYPE_MAP.put("WQ_TRADEMARK", "wzx_trademark/trademark");
    }

    /**
     * 向es的配置文件中追加es的相关配置
     *
     * @param conf       初始化的配置文件
     * @param esId       设置es的主键ID
     * @param esResource es中index/type
     * @param esNodes    es集群服务器nodes
     * @return
     */
    public static Configuration buildWriteESConf(Configuration conf, String esId, String esResource, String esNodes) {
        // es index 自动创建
        conf.set("es.index.auto.create", "true");

        // 指定es index/type
        conf.set("es.resource", esResource);

        /**
         * es.mapping.id (default none) *
         The document field/property name containing the document id.
         */
        // 设置es主键id
        conf.set("es.mapping.id", esId);

        /**
         * es.write.operation (default index) *
         The write operation elasticsearch-hadoop should peform - can be any of:
         index (default) : new data is added while existing data (based on its id) is replaced (reindexed).
         create : adds new data - if the data already exists (based on its id), an exception is thrown.
         update : updates existing data (based on its id). If no data is found, an exception is thrown.
         upsert : known as merge or insert if the data does not exist, updates if the data exists (based on its id).
         */
        //conf.set("es.write.operation", "upsert");

        //很重要,这个属性（当前任务的第几次重试）
        conf.set("mapred.tip.id", "task_201707121733_0003_m_000005");

        //指定es集群服务器的节点
        conf.set("es.nodes", esNodes);

        return conf;
    }


    /**
     * 写数据到es
     *
     * @param javaRDD 待写入的数据javaRDD
     * @param conf    es集群的相关配置文件
     */
    public static void sparkWriteToEs(JavaRDD<String> javaRDD, Configuration conf) {
        javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, NullWritable, MapWritable>() {
            @Override
            public Iterable<Tuple2<NullWritable, MapWritable>> call(Iterator<String> iterator) throws Exception {
                final LinkedList<Tuple2<NullWritable, MapWritable>> list = new LinkedList<Tuple2<NullWritable, MapWritable>>();
                while (iterator.hasNext()) {
                    // 初始化 MapWritable
                    final MapWritable writableMap = new MapWritable();
                    try {
                        // 获取文件的内容
                        final String next = iterator.next();
                        // 将内容解析为json
                        final JSONObject jsonObject = JSONObject.parseObject(next.trim());
                        // 对文件的内容进行遍历
                        final Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
                        // 如果网页 url 不为空，则进行遍历。
                        final String url = jsonObject.getString("url");
                        if (StringUtils.isNotBlank(url)) {
                            // 开始遍历
                            for (Map.Entry<String, Object> entry : entries) {
                                final Object entryValue = entry.getValue();
                                // 判断读取的文件内容是否是BigDecimal，如果是则对数据进行处理。
                                if (entryValue instanceof BigDecimal) {
                                    BigDecimal a = new BigDecimal(((BigDecimal) entryValue).doubleValue());
                                    writableMap.put(new Text(entry.getKey()), WritableUtils.toWritable(a.doubleValue()));
                                } else {
                                    writableMap.put(new Text(entry.getKey()), WritableUtils.toWritable(entryValue));
                                }
                            }

                            list.add(new Tuple2<NullWritable, MapWritable>(null, writableMap));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return list;
            }
        }).saveAsNewAPIHadoopFile("/spark/es/data/", NullWritable.class, MapWritable.class, EsOutputFormat.class, conf);
    }

    /**
     * 构建查询Es的条件
     *
     * @param keyWords
     * @param startTime
     * @param endTime
     * @param taskType
     * @param isNeed
     * @return
     */
    public static String buildQueryESCondition(Map<String, String> keyWords, String startTime, String endTime, int taskType, boolean isNeed, String wzxType) {

        //构建查询es条件的参数
        final String AND_KEYS = keyWords.get(ESUtils.AND_KEYS);
        final String OR_KEYS = keyWords.get(ESUtils.OR_KEYS);
        final String NOT_KEYS = keyWords.get(ESUtils.NOT_KEYS);
        final String sourceQuery = ESUtils.queryKeyStr(AND_KEYS, OR_KEYS, NOT_KEYS);

        StringBuffer sb = new StringBuffer();

        final BusinessType businessType = BusinessType.valueOf(taskType);
        switch (businessType) {
            case WQ_MOVIES: {
                sb.append("(");
                sb.append("title: ").append("(").append(sourceQuery).append(")");
                sb.append(" OR ");
                sb.append("movName: ").append("(").append(sourceQuery).append(")");
                sb.append(")");
            }
            break;
            case WQ_FICTION: {
                sb.append("(");
                sb.append("title: ").append("(").append(sourceQuery).append(")");
                sb.append(" OR ");
                sb.append("bookName: ").append("(").append(sourceQuery).append(")");
                sb.append(")");
            }
            break;
            case WQ_MUSIC: {
                sb.append("(");
                sb.append("title: ").append("(").append(sourceQuery).append(")");
                sb.append(" OR ");
                sb.append("musicName: ").append("(").append(sourceQuery).append(")");
                sb.append(")");
            }
            break;
            case WQ_APP: {
                sb.append("(");
                sb.append("title: ").append("(").append(sourceQuery).append(")");
                sb.append(" OR ");
                sb.append("appName: ").append("(").append(sourceQuery).append(")");
                sb.append(")");
            }
            break;
            case WQ_CARICATURE: {
                sb.append("(");
                sb.append("title: ").append("(").append(sourceQuery).append(")");
                sb.append(" OR ");
                sb.append("caricatureName: ").append("(").append(sourceQuery).append(")");
                sb.append(")");
            }
            break;
            case WQ_SOFTWARE: {
                sb.append("(");
                sb.append("title: ").append("(").append(sourceQuery).append(")");
                sb.append(" OR ");
                sb.append("softName: ").append("(").append(sourceQuery).append(")");
                sb.append(")");
            }
            break;
            case WQ_TRADEMARK: {
                sb.append("(");
                sb.append("goodsName: ").append("(").append("*").append(")");
                sb.append(" OR ");
                sb.append("attributes: ").append("(").append("*").append(")");
                sb.append(")");
            }
            break;

            default:
                break;
        }

        /**
         * 设置查询条件是只要全站抓取数据或站内搜，或者两者都要
         */
        if (StringUtils.isNotBlank(wzxType)) {
            sb.append(" AND wzxType:").append(wzxType);
        }

        if (isNeed) {
            sb.append(" AND dwCreatedAt:[").append(startTime);
            sb.append(" TO ").append(endTime).append("]");
        }

        //查询条件的构造
        HashMap<String, Object> conditionMap = new HashMap<String, Object>(1);
        conditionMap.put("query", ImmutableMap.of("query_string", ImmutableMap.of("query", sb.toString())));

        //SPARK查询ES的查询条件
        String condition = JSON.toJSONString(conditionMap);

        return condition;
    }

    //构建查询Es的配置文件
    public static Configuration buildQueryESConfiguration(Configuration conf, Map<String, String> keyWords, String startTime, String endTime, String esIndexs, String esNodes, int taskType, boolean isNeed, String wzxType) {
        Configuration config = conf;

        //指定读取的索引名称
        config.set(ConfigurationOptions.ES_RESOURCE, esIndexs);

        //正式环境指定es节点
        config.set(ConfigurationOptions.ES_NODES, esNodes);

        config.set(ConfigurationOptions.ES_READ_FIELD_EMPTY_AS_NULL, "false");

        //查询条件
        config.set(ConfigurationOptions.ES_QUERY, buildQueryESCondition(keyWords, startTime, endTime, taskType, isNeed, wzxType));

        return config;
    }

    public static JavaRDD<JSONObject> queryDataFromEs(Configuration conf, JavaSparkContext javaSparkContext, Map<String, String> keyWords, String startTime, String endTime, String esIndexs, String esNodes, int taskType, boolean isNeed, String wzxType) {

        final Configuration configuration = buildQueryESConfiguration(conf, keyWords, startTime, endTime, esIndexs, esNodes, taskType, isNeed, wzxType);

        final JavaRDD<JSONObject> javaRDD = javaSparkContext
                .newAPIHadoopRDD(configuration, EsInputFormat.class, NullWritable.class, MapWritable.class)
                .mapPartitions(new FlatMapFunction<Iterator<Tuple2<NullWritable, MapWritable>>, JSONObject>() {
                                   @Override
                                   public Iterable<JSONObject> call(Iterator<Tuple2<NullWritable, MapWritable>> iterator) throws Exception {
                                       List<JSONObject> resList = new LinkedList<JSONObject>();

                                       while (iterator.hasNext()) {
                                           final Tuple2<NullWritable, MapWritable> next = iterator.next();
                                           final MapWritable mapWritable = next._2();
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
                               }
                );

        return javaRDD;
    }

    /**
     * 拼接查询条件
     */
    public static String queryKeyStr(String wordsIntact, String wordsOr, String wordsNot) {
        //关键词字符串
        StringBuffer queryKwStr = new StringBuffer();

        //AND关键词
        String[] intactArr = null;
        if (StringUtils.isNotBlank(wordsIntact)) {
            intactArr = wordsIntact.trim().split("[|]");
            queryKwStr.append("(");
            if (intactArr.length > 0) {
                for (int i = 0; i < intactArr.length; i++) {
                    if (StringUtils.isNotBlank(intactArr[i])) {
                        queryKwStr.append("\"" + intactArr[i].trim() + "\"");
                        if (i < intactArr.length - 1) {
                            queryKwStr.append(" AND ");
                        }
                    }
                }
            }
            queryKwStr.append(")");
        }

        //OR关键词
        if (StringUtils.isNotBlank(wordsOr)) {
            //如果第一行关键词为空,不拼接AND
            if (intactArr != null && intactArr.length > 0) {
                queryKwStr.append(" AND (");
            } else {
                queryKwStr.append(" (");
            }
            String orArr[] = wordsOr.trim().split("[|]");
            if (orArr.length > 0) {
                for (int i = 0; i < orArr.length; i++) {
                    if (StringUtils.isNotBlank(orArr[i])) {
                        queryKwStr.append("\"" + orArr[i].trim() + "\"");
                        if (i < orArr.length - 1) {
                            queryKwStr.append(" OR ");
                        }
                    }
                }
            }
            queryKwStr.append(")");
        }

        //NOT关键词
        if (StringUtils.isNotBlank(wordsNot)) {
            String[] notArr = wordsNot.trim().split("[|]");
            for (int i = 0; i < notArr.length; i++) {
                if (StringUtils.isNotBlank(notArr[i])) {
                    queryKwStr.append(" NOT \"" + notArr[i].trim() + "\"");
                }
            }
        }

        return queryKwStr.toString();
    }
}
