///*
//package util;
//
//import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
//import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
//import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchType;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.text.Text;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//*/
///**
// * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html
// * elasticSearch 操作
// *//*
//
//
//public class Esutil {
//    public static Client client = null;
//
//
//    */
///**
//     * 获取客户端
//     *
//     * @return
//     *//*
//
//    public static Client getClient() {
//        if (client != null) {
//            return client;
//        }
//        Settings settings = Settings.settingsBuilder().put("cluster.name", "sd-es-2.3.3").build();
//        try {
//            client = TransportClient.builder().settings(settings).build()
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.235"), 9300))
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.237"), 9300))
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.238"), 9300));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return client;
//    }
//
//
//    */
///**
//     * 单条数据插入
//     *
//     * @param index
//     * @param type
//     * @param doc
//     * @return
//     *//*
//
//    public static String addIndex(String index, String type, Doc doc) {
//        HashMap<String, Object> hashMap = new HashMap<String, Object>();
//        hashMap.put("id", doc.getId());
//        hashMap.put("title", doc.getTitle());
//        hashMap.put("describe", doc.getDescribe());
//        hashMap.put("author", doc.getAuthor());
//        IndexResponse response = getClient().prepareIndex(index, type).setSource(hashMap).execute().actionGet();
//        return response.getId();
//    }
//
//
//    */
///**
//     * 批量数据插入
//     *
//     * @param index
//     * @param type
//     * @param list
//     *//*
//
//    public static void addIndexbath(String index, String type, List<Doc> list) {
//        BulkRequestBuilder bulkRequest = client.prepareBulk();
//        final int size = list.size();
//        for (int i = 0; i < size; i++) {
//            final Doc doc = list.get(i);
//            final HashMap<String, Object> hashMap = new HashMap<String, Object>();
//            hashMap.put("id", doc.getId());
//            hashMap.put("title", doc.getTitle());
//            hashMap.put("describe", doc.getDescribe());
//            hashMap.put("author", doc.getAuthor());
//            bulkRequest.add(client.prepareIndex(index, type).setSource(hashMap));
//
//            // 每10000条提交一次
//            if (i % 10000 == 0) {
//                bulkRequest.execute().actionGet();
//            }
//
//            // 处理最后的
//            bulkRequest.execute().actionGet();
//        }
//    }
//
//    */
///**
//     * 搜索查询
//     *
//     * @param key
//     * @param index
//     * @param type
//     * @param start
//     * @param row
//     * @return
//     *//*
//
//    public static Map<String, Object> search(String key, String index, String type, int start, int row) {
//        SearchRequestBuilder builder = getClient().prepareSearch(index);
//        builder.setTypes(type);
//        builder.setFrom(start);
//        builder.setSize(row);
//        //设置高亮字段名称
//        builder.addHighlightedField("title");
//        builder.addHighlightedField("type");
//        //设置高亮前缀
//        builder.setHighlighterPreTags("<font color='red' >");
//        //设置高亮后缀
//        builder.setHighlighterPostTags("</font>");
//        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
//        if (StringUtils.isNotBlank(key)) {
//            //builder.setQuery(QueryBuilders.termQuery("title",key));
//            builder.setQuery(QueryBuilders.multiMatchQuery(key, "title", "type"));
//        }
//        builder.setExplain(true);
//        SearchResponse searchResponse = builder.get();
//
//        SearchHits hits = searchResponse.getHits();
//        long total = hits.getTotalHits();
//        Map<String, Object> map = new HashMap<String, Object>();
//        SearchHit[] hits2 = hits.getHits();
//        map.put("count", total);
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        for (SearchHit searchHit : hits2) {
//            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
//            HighlightField highlightField = highlightFields.get("title");
//            Map<String, Object> source = searchHit.getSource();
//            if (highlightField != null) {
//                Text[] fragments = highlightField.fragments();
//                String name = "";
//                for (Text text : fragments) {
//                    name += text;
//                }
//                source.put("title", name);
//            }
//            HighlightField highlightField2 = highlightFields.get("type");
//            if (highlightField2 != null) {
//                Text[] fragments = highlightField2.fragments();
//                String describe = "";
//                for (Text text : fragments) {
//                    describe += text;
//                }
//                source.put("type", describe);
//            }
//            list.add(source);
//        }
//        map.put("dataList", list);
//        return map;
//    }
//
//
//    */
///**
//     * 删除索引库
//     *
//     * @param indexName
//     *//*
//
//    public static void deleteIndex(String indexName) {
//        try {
//            if (!isIndexExists(indexName)) {
//                System.out.println(indexName + " not exists");
//            } else {
//
//                DeleteIndexResponse dResponse = getClient().admin().indices().prepareDelete(indexName)
//                        .execute().actionGet();
//                if (dResponse.isAcknowledged()) {
//                    System.out.println("delete index " + indexName + "  successfully!");
//                } else {
//                    System.out.println("Fail to delete index " + indexName);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    */
///**
//     * 创建索引库
//     *
//     * @param indexName
//     *//*
//
//    public static void createIndex(String indexName) {
//        try {
//            // 创建索引库
//            if (isIndexExists("indexName")) {
//                System.out.println("Index  " + indexName + " already exits!");
//            } else {
//                CreateIndexRequest cIndexRequest = new CreateIndexRequest("indexName");
//                CreateIndexResponse cIndexResponse = getClient().admin().indices().create(cIndexRequest)
//                        .actionGet();
//                if (cIndexResponse.isAcknowledged()) {
//                    System.out.println("create index successfully！");
//                } else {
//                    System.out.println("Fail to create index!");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    */
///**
//     * 判断索引是否存在 传入参数为索引库名称
//     *
//     * @param indexName
//     * @return
//     *//*
//
//    public static boolean isIndexExists(String indexName) {
//        boolean flag = false;
//        try {
//            IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);
//
//            IndicesExistsResponse inExistsResponse = getClient().admin().indices()
//                    .exists(inExistsRequest).actionGet();
//
//            if (inExistsResponse.isExists()) {
//                flag = true;
//            } else {
//                flag = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return flag;
//    }
//
//    */
///**
//     * 测试
//     *
//     * @param args
//     *//*
//
//    public static void main(String[] args) {
//        Map<String, Object> search = Esutil.search("es", "sdyc_test", "test", 0, 100);
//        List<Map<String, Object>> list = (List<Map<String, Object>>) search.get("dataList");
//        if (list.size() == 0) {
//            System.out.println("没有查询到数据...");
//        } else {
//            for (Map<String, Object> map : list) {
//                System.out.println("=======================================================");
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    System.out.println(entry.getKey() + "->" + entry.getValue() + " 【" + entry.getValue().getClass() + "】");
//                }
//            }
//        }
//        */
///*System.out.println("开始入数据");
//        Doc doc=new Doc();
//        doc.setId(10);
//        doc.setAuthor("CHONGE");
//        doc.setDescribe("简单的测试");
//        doc.setTitle("es到底是什么鬼");
//        Esutil.addIndex("sdyc_test", "test", doc);
//        System.out.println("导入成功");*//*
//
//        //Esutil.deleteIndex("movies");
//    }
//}
//*/
