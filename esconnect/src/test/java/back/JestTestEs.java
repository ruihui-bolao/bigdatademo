/*
package back;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.core.Search;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.LinkedHashSet;
import java.util.Map;

*/
/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/14 10:43
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 *//*

public class JestTestEs {

    public JestClient getClient() throws Exception{

        String connectionUrl2= "http://192.168.1.237:9200";
        String connectionUrl1 ="http://192.168.1.235:9200";
        String connectionUrl3= "http://192.168.1.238:9200";
        LinkedHashSet servers = new LinkedHashSet();
        servers.add(connectionUrl1);
        servers.add(connectionUrl2);
        servers.add(connectionUrl3);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getServerProperties().put(ClientConstants.SERVER_LIST, servers);
        clientConfig.getClientFeatures().put(ClientConstants.IS_MULTI_THREADED, false);

        JestClientFactory jestClientFactory = new JestClientFactory();
        jestClientFactory.setClientConfig(clientConfig);
        return jestClientFactory.getObject();
    }

    public void  jestSearch(String key) throws Exception{
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(key,"title","type"));
        // 匹配所有的格式
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        Search search = new Search(Search.createQueryWithBuilder(searchSourceBuilder.toString()));
        search.addIndex("zhfwxt");
        JestClient client = getClient();
        JestResult result = client.execute(search);
        Map resMap = result.getJsonMap();
        Object hits = resMap.get("hits");
        
        System.out.println(hits.toString());
    }

    public static void main(String[] args)  throws Exception{
        JestTestEs jestTestEs = new JestTestEs();
        jestTestEs.jestSearch("zhfwxt");
    }

}
*/
