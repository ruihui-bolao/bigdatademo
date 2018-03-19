package flume.httpsource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.http.HTTPSourceHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/19 10:17
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    flume http source 对应的 httphandle .
 */
public class FlumeHttpHandle implements HTTPSourceHandler {

    /**
     * flume event headers
     */
    public final static String HEADERS = "headers";

    /**
     * flume event body
     */
    public final static String BODY = "body";

    /**
     * flume 处理文件的编码格式
     */
    public final static String charset = "UTF-8";

    /**
     * hadoop 的上下文机制
     */
    public final static Configuration HADOOP_CONF = new Configuration();

    /**
     * hadoop 的文件系统
     */
    public final static FileSystem HADOOP_FS;

    /**
     * 日志输出系统
     */
    private static final Logger LOG = LoggerFactory.getLogger(FlumeHttpHandle.class);

    static {
        try {
            HADOOP_FS = FileSystem.get(HADOOP_CONF);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 实现 HTTPSourceHandler 接口，处理 http 请求响应
     *
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    public List<Event> getEvents(HttpServletRequest httpServletRequest) throws Exception {
        // 将 HTTP 请求的数据 转换为 str
        final String str = IOUtils.toString(httpServletRequest.getInputStream(), charset);
        // 对 http 请求字符串进行处理
        return parseEvent(str);
    }

    /**
     * 处理 http 请求的字符串
     *
     * @param jsonString http 的请求字符串
     * @return
     */
    public List<Event> parseEvent(String jsonString) {
        Object parse = JSON.parse(jsonString);
        // 如果是 jsonObject
        if (parse instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) parse;
            // 获取 headers
            JSONObject headers = jsonObject.getJSONObject(HEADERS);
            // 将 headers 存放在 map 中
            HashMap<String, String> map = new HashMap<String, String>(headers.size());
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                map.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
            // 获取body
            JSONObject body = jsonObject.getJSONObject(BODY);
            //读取 HTTP 请求的 headers 和 body .
            return readEvents(map, body);
        } else if (parse instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) parse;
            ArrayList<Event> list = new ArrayList<Event>();
            for (Object o : jsonArray) {
                JSONObject json = (JSONObject) o;
                JSONObject header = json.getJSONObject(HEADERS);
                JSONObject body = json.getJSONObject(BODY);
                HashMap<String, String> map = new HashMap<String, String>(header.size());
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    map.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                List<Event> events = readEvents(map, body);
                list.addAll(events);
            }
            return list;
        } else {
            throw new IllegalArgumentException("unsupport result: " + jsonString);
        }
    }

    /**
     * 根据headers 和 body 重中读取数据
     *
     * @param headers event jheaders
     * @param body    event body
     * @return
     */
    public List<Event> readEvents(Map<String, String> headers, JSONObject body) {

        // 获取其中的数据类型
        final String dataType = body.getString("dataType");
        if (dataType != null) {
            headers.put("dataType", dataType);
        }

        // 获取其中的 projectid
        final String projectId = body.getString("projectId");
        if (projectId != null) {
            headers.put("projectId", projectId);
        }

        // 获取其中的path 路径
        String path = body.getString("path");
        if (StringUtils.isBlank(path)) {
            return new ArrayList<Event>(0);
        }

        // hdfs 读取 文件路径
        FSDataInputStream fsDataInputStream = null;
        try {
            Path f = new Path(path);
            if (!HADOOP_FS.exists(f)) {
                LOG.info(path + "文件不存在！");
                return new ArrayList<Event>(0);
            }
            LOG.info("开始读取文件：{}", f.toString());
            fsDataInputStream = HADOOP_FS.open(f);
            List<String> lines = IOUtils.readLines(fsDataInputStream, charset);
            ArrayList<Event> events = new ArrayList<Event>(lines.size());
            for (String line : lines) {
                // 构建 flume event 并添加到 events
                events.add(EventBuilder.withBody(line, Charset.forName(charset),headers));
            }
            LOG.info("文件：{}, line count: {}", f.toString(), events.size());
            return events;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(fsDataInputStream != null){
                    fsDataInputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return new ArrayList<Event>(0);
    }

    @Override
    public void configure(Context context) {
        String charset = context.getString("charset", "UTF-8");
    }
}
