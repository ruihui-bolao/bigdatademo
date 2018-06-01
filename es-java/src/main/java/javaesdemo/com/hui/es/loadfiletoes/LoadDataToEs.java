package javaesdemo.com.hui.es.loadfiletoes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/31 10:07
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   将解析的数据批量导入es中(数据是以json格式存储，一条数据是一个json，数据之间用回车分割。)
 */
public class LoadDataToEs {

    /**
     * es 集群的连接
     */
    private Client client;

    /**
     * 构造器初始化
     *
     * @param clusterName 集群名字
     * @param esHosts     集群ip  "10.10.2.1:9300,10.10.2.2:9300"
     */
    public LoadDataToEs(String clusterName, String esHosts) {
        // 初始化设置
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();

        // 初始化连接
        TransportClient client = TransportClient.builder().settings(settings).build();
        try {
            // 将es集群添加到连接中
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
     *  将
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
            BulkRequestBuilder bulkRequestTrans = client.prepareBulk();
            while ((line = reader.readLine()) != null) {
                JSONObject blockJson = JSON.parseObject(line);
                JSONArray transactions = blockJson.getJSONArray("Transactions");
                blockJson.remove("Transactions");

                // 准备写入block表
                String blockIndex = blockJson.getString("BlockHash");
                bulkRequestBlock.add(client.prepareIndex("bitcoin", "block").setSource(blockJson).setId(blockIndex));

                // 准备写入transaction表
                for (Object transaction : transactions) {
                    JSONObject transJson = JSON.parseObject(String.valueOf(transaction));
                    String transIndex = transJson.getString("TransactionHash");
                    String blockHash = transJson.getString("BlockHash");
                    bulkRequestTrans.add(client.prepareIndex("bitcoin", "transaction").setSource(transJson).setId(transIndex).setParent(blockHash));
                }

                if (count % 1000 == 0) {
                    batch++;
                    bulkRequestBlock.execute().actionGet();
                    bulkRequestTrans.execute().actionGet();
                    String s = String.format("第" + batch + "次提交了:(" + count + ")");
                    System.out.println(s);
                    bulkRequestBlock.request().requests().clear();
                    bulkRequestTrans.request().requests().clear();
                }
                count++;
            }
            bulkRequestBlock.execute().actionGet();
            bulkRequestTrans.execute().actionGet();
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
        String filePath = "C:\\Users\\sssd\\Desktop\\estest.txt";
        LoadDataToEs loadDataToEs = new LoadDataToEs(clusterName, hosts);
        boolean b = loadDataToEs.importData(filePath);
        if (b) {
            System.out.println("插入完毕!");
        }
    }
}
