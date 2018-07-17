package javaesdemo.com.hui.es.TransportClientEs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/1/3 15:47
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    java 对 es 的相关操作
 */
public class ElastaicTest {

    /**
     * 构建 TransportClient
     */
    TransportClient transportClient;

    /**
     * es索引(相当于传统数据库中的库名)
     */
    String index = "sdyc_test";

    /**
     * 类型名（相当于传统数据中的表名）
     */
    String type = "test";

    public ElastaicTest() throws Exception {

        /**
         * 1, 通过 setting 对象来指定集群配置信息
         */
        Settings.Builder builder = Settings.settingsBuilder();
        builder.put("cluster.name", "sd-es-2.3.3")
                .put("client.transport.sniff", true)
                .build();
        Settings settings = builder.build();

        /**
         *  通过 tcp 协议 构建连接
         */
        TransportClient.Builder transportBuild = TransportClient.builder();
        TransportClient client1 = transportBuild.settings(settings).build();
        transportClient = client1.addTransportAddress((new InetSocketTransportAddress(new InetSocketAddress("192.168.1.235", 9300))));
        /**
         * 3,查看集群消息
         * 注意集群节点：131的elasticsearch.yml中指定为主节点不能存储数据，
         *              128的elasticsearch.yml中指定不为主节点只能存储数据。
         * 所以控制台只打印了192.168.79.128,只能获取数据节点。
         */
        List<DiscoveryNode> discoveryNodes = transportClient.connectedNodes();
        for (DiscoveryNode node : discoveryNodes) {
            System.out.println(node.getHostAddress());
        }

    }

    /**
     * 获取es对应要查询的文档
     *
     * @param esId es中的主键id
     */
    public void get(String esId) {
        esId = "AWAvpQteeT8FH53rFzZY";
        GetResponse getResponse = transportClient.prepareGet(index, type, esId).get();    // index;es索引，type:es表格，id：对应es表格中的_id.
        System.out.println(getResponse.getSourceAsString());
    }

    /**
     * 向 es 中插入数据，参数为 json 字符串
     *
     * @param jsonStr json 字符串
     */
    public void indexJson(String jsonStr) {
        jsonStr = "{\"author\":\"ccc\",\"id\":8}";
        IndexResponse indexResponse = transportClient
                .prepareIndex(index, type, "3").setSource(jsonStr).get();    // 其中的 id 为 es 表格中的主键
        System.out.println(indexResponse.getVersion());
    }

    /**
     * 向 es 中插入数据，参数为map<String,Object>
     *
     * @param values map类型的数据格式
     */
    public void indexMap(HashMap<String, Object> values) {
        HashMap<String, Object> source = new HashMap<String, Object>(2);
        source.put("author", "ccc");
        source.put("id", "12");
        values = source;
        IndexResponse indexResponse = transportClient.prepareIndex(index, type, "4").setSource(values).get();    //其中的 id 为 es 表格中的主键。
        System.out.println(indexResponse.getVersion());
    }

    /**
     * 向 es 中插入数据，参数为javaBean
     *
     * @param valueBean javabean 类型的数据格式
     * @throws Exception
     */
    public void indexBean(Student valueBean) throws Exception {
        Student student = new Student();
        student.setAuthor("hui");
        student.setId(21);
        valueBean = student;
        ObjectMapper mapper = new ObjectMapper();
        IndexResponse indexResponse = transportClient.prepareIndex(index, type, "5").setSource(mapper.writeValueAsString(valueBean)).get();
        System.out.println(indexResponse.getVersion());
    }

    /**
     * 删除es指定的文档
     *
     * @param id es 主键id
     */
    public void deleteIndex(String id) {
        id = "21";
        DeleteResponse deleteResponse = transportClient.prepareDelete(index, type, id).get();
        System.out.println(deleteResponse.getVersion());
    }

    /**
     * 计算索引库文档总数
     */
    public void count() {
        long count = transportClient.prepareCount(index).get().getCount();
        System.out.println(count);
    }

    /**
     * 通过 bulk 执行批处理
     */
    public void bulk() throws Exception {

        //1, 生成 bulk
        BulkRequestBuilder bulk = transportClient.prepareBulk();

        //2，新增
        IndexRequest add = new IndexRequest(index, type, "10");
        add.source(XContentFactory.jsonBuilder().startObject()
                .field("author", "hui").field("id", "25")
                .endObject());

        //3，删除
        DeleteRequest del = new DeleteRequest(index, type, "3");

        //4, 修改
        XContentBuilder source = XContentFactory.jsonBuilder().startObject().field("author", "hui01").field("id", "2501").endObject();
        UpdateRequest update = new UpdateRequest(index, type, "4");
        update.doc(source);
        bulk.add(del);     //添加删除功能
        bulk.add(add);     //添加新增功能
        bulk.add(update);  //添加修改功能

        //5，执行批次处理
        BulkResponse bulkResponse = bulk.get();
        if (bulkResponse.hasFailures()) {
            BulkItemResponse[] items = bulkResponse.getItems();
            for (BulkItemResponse item : items) {
                System.out.println(item.getFailureMessage());
            }
        } else {
            System.out.println("全部执行成功！");
        }
    }

    /**
     * 查询索引库
     */
    public void testSearch() {
        SearchResponse searchResponse = transportClient.prepareSearch(index).setTypes(type).setQuery(QueryBuilders.matchAllQuery())    //查询所有
                .setQuery(QueryBuilders.matchQuery("name", "tom").operator(MatchQueryBuilder.Operator.AND))  // 根据tom分词查询name,默认or
                .setQuery(QueryBuilders.multiMatchQuery("tom", "name", "age"))  //指定查询的字段
                .setQuery(QueryBuilders.queryStringQuery("name:to* AND age:[0 TO 19]")) //根据条件查询,支持通配符大于等于0小于等于19
                .setQuery(QueryBuilders.termQuery("name", "tom"))   //查询时不分词
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setFrom(0).setSize(10)    //分页
                .addSort("age", SortOrder.DESC)  //排序
                .get();
        SearchHits hits = searchResponse.getHits();
        long total = hits.getTotalHits();
        System.out.println(total);
        SearchHit[] searchHits = hits.hits();
        for (SearchHit s : searchHits) {
            System.out.println(s.getSourceAsString());
        }
    }


    public static void main(String[] args) throws Exception {
        ElastaicTest elastaicTest = new ElastaicTest();
        elastaicTest.bulk();
    }
}
