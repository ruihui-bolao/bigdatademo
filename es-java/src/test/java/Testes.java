import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/7/20 15:43
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Testes {

    public static Client client;

    public Testes(String clusterName, String esHosts) {
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        TransportClient client = TransportClient.builder().settings(settings).build();

        // 初始化连接
        try {
            String[] nodes = esHosts.split(",");
            for (String node : nodes) {
                if (node.length() > 0) {
                    String[] hostPost = node.split(":");
                    this.client = client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostPost[0]), Integer.parseInt(hostPost[1])));
                }
            }
            System.out.println("初始化连接完成！！！");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void queryData(){

//        QueryBuilders.commonTermsQuery()
        QueryBuilder queryBuilder = QueryBuilders.commonTermsQuery("USER_ID","2474310017");
        SearchResponse response = client.prepareSearch("hui_test_post")
                .setTypes("post_test")
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .execute()
                .actionGet();
        //获取命中数
        System.out.println(response.getHits().totalHits());
      /*  //获取响应字符串
        System.out.println(response.toString());
        //遍历查询结果输出相关度分值和文档内容
        SearchHits searchHits =  response.getHits();
        for(SearchHit searchHit : searchHits) {
            System.out.println(searchHit.getScore());
            System.out.println(searchHit.getSourceAsString());
        }*/
    }


    public void  downData(){
        SearchResponse response = client.prepareSearch("hui_test_post").setTypes("post_test")
                .setQuery(QueryBuilders.matchAllQuery()).setSize(100000).setScroll(new TimeValue(1000000))
                .setSearchType(SearchType.SCAN).execute().actionGet();
        String scrollid = response.getScrollId();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\sssd\\Desktop\\es.txt", true));
            while (true) {
                SearchResponse response2 = client.prepareSearchScroll(scrollid).setScroll(new TimeValue(1000000))
                        .execute().actionGet();
                SearchHits searchHit = response2.getHits();
                //再次查询不到数据时跳出循环
                if (searchHit.getHits().length == 0) {
                    break;
                }
                System.out.println("查询数量 ：" + searchHit.getHits().length);
                for (int i = 0; i < searchHit.getHits().length; i++) {
                    String json = searchHit.getHits()[i].getSourceAsString();
                    out.write(json);
                    out.write("\r\n");
                }
            }
            System.out.println("查询结束");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean importData(String sourcePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
            // 初始化数据计数
            int count = 1;
            // 初始化提交批次
            int batch = 0;
            // 初始化行内容
            String line = null;
            // 批量提交数据
            BulkRequestBuilder bulkRequestBlock = client.prepareBulk();
            while ((line = reader.readLine()) != null) {
                JSONObject blockJson = JSON.parseObject(line);
                blockJson.put("data", new Date());
                bulkRequestBlock.add(client.prepareIndex("hui_test_post", "post_test").setSource(blockJson).setId(blockJson.getString("POST_URN")));
                if (count % 1000 == 0) {
                    batch++;
                    bulkRequestBlock.execute().actionGet();
                    String s = String.format("第" + batch + "次提交了:(" + count + ")");
                    System.out.println(s);
                    bulkRequestBlock.request().requests().clear();
                }
                count++;
            }
            bulkRequestBlock.execute().actionGet();
            reader.close();
            client.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用来删除某个字段
     *
     */
    public void deleteField(){
        client.prepareUpdate("hui_test_post", "post_test", "195861-4264009153288606").setScript(new Script(     "ctx._source.remove(\"userlocation\")",ScriptService.ScriptType.INLINE, null, null)).get();
    }

    public static void main(String[] args) {
        String clusterName = "sd-es-2.3.3";
        String hosts = "192.168.1.235:9300,192.168.1.237:9300,192.168.1.238:9300";
        String filePath = "C:\\Users\\sssd\\Desktop\\newes.txt";
        Testes testes = new Testes(clusterName, hosts);
        testes.importData(filePath);
//        testes.downData();
    }

}
