package javaesdemo.com.hui.es.loadfiletoes;

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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/31 10:07
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   将本地的文件导入到es
 */
public class LoadDataToEsTemp {

    /**
     * es 集群的连接
     */
    public static Client client;

    /**
     * 构造器初始化
     *
     * @param clusterName 集群名字
     * @param esHosts     集群ip  "10.10.2.1:9300,10.10.2.2:9300"
     */
    public LoadDataToEsTemp(String clusterName, String esHosts) {
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


    /**
     * 将本地文件通过Java client 的方式导入到es中
     *
     * @param sourcePath
     * @return
     */
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
                bulkRequestBlock.add(client.prepareIndex("hui_test_post", "post_test").setSource(blockJson).setId(blockJson.getString("myid")));
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

    public static void main(String[] args) {
        String clusterName = "sd-es-2.3.3";
        String hosts = "192.168.1.235:9300,192.168.1.237:9300,192.168.1.238:9300";
        String filePath = "C:\\Users\\sssd\\Desktop\\post.json";
        LoadDataToEsTemp loadDataToEs = new LoadDataToEsTemp(clusterName, hosts);

       /* boolean b = loadDataToEs.importData(filePath);
        if (b) {
            System.out.println("插入完毕!");
        }*/
    }
}
