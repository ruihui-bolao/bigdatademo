package esconnect;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/14 10:43
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class JestTestEs {

    /**
     * jest 通过 JestClientFactory 获取连接
     *
     * @return
     * @throws Exception
     */
    public JestClient getClient() throws Exception {

        // es 集群的 IP host和 port
        String connectionUrl2 = "http://192.168.1.237:9200";
        String connectionUrl1 = "http://192.168.1.235:9200";
        String connectionUrl3 = "http://192.168.1.238:9200";

        // 添加集群IP地址到 set 中
        LinkedHashSet servers = new LinkedHashSet();
        servers.add(connectionUrl1);
        servers.add(connectionUrl2);
        servers.add(connectionUrl3);

        // 初始化 httpClientConfig 的配置文件，并通过JestClientFactory 创建 JestClient
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder(servers).multiThreaded(true).build();
        JestClientFactory jestClientFactory = new JestClientFactory();
        jestClientFactory.setHttpClientConfig(httpClientConfig);
        return jestClientFactory.getObject();
    }

    /**
     *  Jest 通过 HTTP 请求执行 es 查询命令
     * @throws Exception
     */
    public void jestSearch() throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // searchSourceBuilder.query(QueryBuilders.multiMatchQuery(key,"title","type"));  // 通过关键词匹配指定的列
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());  // 匹配所有的格式

        // 初始化查询语句，其中 addIndex 为索引，相当于数据库中的表名
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("zhfwxt").build();
        JestClient client = getClient();

        // 连接执行 search 语句
        SearchResult execute = client.execute(search);
        Integer total = execute.getTotal();    // 获取查询结果的总数
        System.out.println(total);
    }

    /**
     *  将文件 上传到 es 中
     * @param key
     * @throws Exception
     */
    public void contextLoads(String key) throws Exception{
        JestClient client = getClient();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        // 对数据一个一个的进行插入
        for (Integer integer : list) {
            Index index = new Index.Builder(integer).index("zhfwxt").type("fulltext").id(Integer.toString(list.indexOf(integer))).build();
            System.out.println("添加索引-----》" + integer);
            client.execute(index);
        }

        // 批量操作
        Bulk.Builder builder = new Bulk.Builder();
        for (Integer integer : list) {
            Index index = new Index.Builder(integer).index("zhfwxt").type("fulltext").id(Integer.toString(list.indexOf(integer))).build();
            builder.addAction(index);
        }
        client.execute(builder.build());
        client.shutdownClient();
    }


    /**
     * 测试 jest clent
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        JestTestEs jestTestEs = new JestTestEs();
        jestTestEs.jestSearch();
    }

}
