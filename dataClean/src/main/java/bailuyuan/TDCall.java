package bailuyuan;

import bailuyuan.service.TDDataService;
import bailuyuan.service.TDDataServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/14 15:24
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TDCall {

    public static final Log LOG = LogFactory.getLog(TDCall.class);
    public final static String apikey = "a7f89e625d9f4b66aa2a2f4fe84af371";
    public final static String apitoken = "542b159907b24c249703300fd4e949f6";
    public static TDDataService tdDataService = null;
    public String X_Access_Token;

    public TDCall(String X_Access_Token) {
        this.X_Access_Token = X_Access_Token;
    }

    static {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:jdbcContext.xml");
        tdDataService = (TDDataServiceImpl) context.getBean("tdDataService");
    }

    /**
     * 调用各接口,获取数据,并解析
     *
     * @param client
     * @param id
     * @return
     * @throws Exception
     */
    public String parserData(CloseableHttpClient client, String id) throws Exception {
        List<String> tagList = new ArrayList<String>();

        //=================================================================================//
        //游戏兴趣标签查询接口
        //=================================================================================//
        final String gameurl = "https://api.talkingdata.com/data/user-tag-game/v1?";
        final JSONArray gametag = getTagDataByTdUrl(client, gameurl, "mac", id, "game_info", null);
        final ArrayList<String> gameTagList = new ArrayList<String>();

        if (gametag != null && gametag.size() != 0) {
            for (Object obj : gametag) {
                final JSONObject jsonObject = (JSONObject) obj;
                final String name = jsonObject.getString("name");
                final String weight = jsonObject.getString("weight");
                //final String label = jsonObject.getString("label");
                gameTagList.add(name + ":" + weight);
            }
        } else {
            gameTagList.add("");
        }
        String gameTagString = StringUtils.join(gameTagList, "|");
        tagList.add(gameTagString);


        //=================================================================================//
        //应用兴趣标签查询接口
        //=================================================================================//
        final String appurl = "https://api.talkingdata.com/data/user-tag-app/v1?";

        final JSONArray apptag = getTagDataByTdUrl(client, appurl, "mac", id, "app_info", null);

        final ArrayList<String> appTagList = new ArrayList<String>();

        if (apptag != null && apptag.size() != 0) {
            for (Object obj : apptag) {
                final JSONObject jsonObject = (JSONObject) obj;
                final String name = jsonObject.getString("name");
                final String weight = jsonObject.getString("weight");
                //final String label = jsonObject.getString("label");
                appTagList.add(name + ":" + weight);
            }
        } else {
            appTagList.add("");
        }

        String appTagString = StringUtils.join(appTagList, "|");
        tagList.add(appTagString);


        //=================================================================================//
        //人口属信息标签性查询接口
        //=================================================================================//
        final String demographicurl = "https://api.talkingdata.com/data/user-tag-demographic/v1?";
        final JSONArray demographictag = getTagDataByTdUrl(client, demographicurl, "mac", id, "person_info", null);

        final ArrayList<String> demographicTagList = new ArrayList<String>();

        Double manV = 0.0d;
        Double womanV = 0.0d;

        if (demographictag != null && demographictag.size() != 0) {
            for (Object obj : demographictag) {
                final JSONObject jsonObject = (JSONObject) obj;
                final String name = jsonObject.getString("name");
                final String weight = jsonObject.getString("weight");
                //final String label = jsonObject.getString("label");

                final int indexOf = name.indexOf("-");
                String tagName = "";
                if (indexOf != -1) {
                    tagName = name.substring(indexOf + 1);
                } else {
                    tagName = name;
                }

                if (StringUtils.equalsIgnoreCase("男", tagName)) {
                    manV = Double.valueOf(weight);
                } else if (StringUtils.equalsIgnoreCase("女", tagName)) {
                    womanV = Double.valueOf(weight);
                } else if (StringUtils.isNotEmpty(tagName)) {
                    demographicTagList.add(tagName + ":" + weight);
                } else {
                    continue;
                }
            }
            final String str = manV >= womanV ? "男:" + String.valueOf(manV) : ("女:" + String.valueOf(womanV));

            demographicTagList.add(str);

        } else {
            demographicTagList.add("");
        }

        String demographicTagString = StringUtils.join(demographicTagList, "|");
        tagList.add(demographicTagString);


        //=============================================================================//
        //线下消费偏好信息查询
        //=============================================================================//
        final String consumeurl = "https://api.talkingdata.com/data/user-tag-consume/v1?";
        final JSONArray consumetag = getTagDataByTdUrl(client, consumeurl, "mac", id, "consume_info", null);
        final ArrayList<String> consumeTagList = new ArrayList<String>();

        if (consumetag != null && consumetag.size() != 0) {
            for (Object obj : consumetag) {
                final JSONObject jsonObject = (JSONObject) obj;
                final String name = jsonObject.getString("name");
                final String weight = jsonObject.getString("weight");
                //final String label = jsonObject.getString("label");
                consumeTagList.add(name + ":" + weight);
            }
        } else {
            consumeTagList.add("");
        }

        String consumeTagString = StringUtils.join(consumeTagList, "|");
        tagList.add(consumeTagString);

/*        //==================================================================================//
        //常在城市信息查询接口
        //==================================================================================//
        final String cityurl = "https://api.talkingdata.com/data/user-loc-city/v1?";

        final JSONArray citytag = getCityDataByMonth(client, cityurl, id, 1);

        final ArrayList<String> cityTagList = new ArrayList<String>();

        if (citytag != null && citytag.size() != 0) {
            for (Object obj : citytag) {
                final JSONObject jsonObject = (JSONObject) obj;
                final String citys = jsonObject.getString("citys");
                if (citys.contains(",")) {
                    final String[] split = citys.split(",");
                    for (String s : split) {
                        cityTagList.add(s);
                    }
                } else {
                    cityTagList.add(citys);
                }
            }
        } else {
            cityTagList.add("");
        }

        String cityTagListString = StringUtils.join(cityTagList, "|");
        tagList.add(cityTagListString);*/

        //==================================================================================//
        //常在城市信息查询接口
        //==================================================================================//
        final String cityurl = "https://api.talkingdata.com/data/user-loc-city/v2?";

        final JSONArray citytag = getCityDataByMonth(client, cityurl, id, 1);

        final ArrayList<String> cityTagList = new ArrayList<String>();

        if (citytag != null && citytag.size() != 0) {
            for (Object obj : citytag) {
                final JSONObject jsonObject = (JSONObject) obj;
                Object location = jsonObject.get("location");
                if (location != null && location instanceof JSONArray) {
                    JSONArray array = (JSONArray) location;
                    if (array.size() > 0) {
                        for (Object o : array) {
                            JSONObject object = (JSONObject) o;
                            cityTagList.add(object.getString("province") + object.getString("city"));
                        }
                    } else {
                        cityTagList.add("");
                    }
                } else {
                    cityTagList.add("");
                }
            }
        } else {
            cityTagList.add("");
        }

        String cityTagListString = StringUtils.join(cityTagList, "|");
        tagList.add(cityTagListString);


        //=================================================================================//
        //设备属性信息查询接口
        //=================================================================================//
        final String deviceurl = "https://api.talkingdata.com/data/user-tag-device/v1?";
        final JSONArray devicetag = getTagDataByTdUrl(client, deviceurl, "mac", id, "device_info", null);

        final ArrayList<String> deviceTagList = new ArrayList<String>();

        if (devicetag != null && devicetag.size() != 0) {
            for (Object obj : devicetag) {
                final JSONObject jsonObject = (JSONObject) obj;
                try {
                    Map<String, String> map = (Map) JSON.parseObject(jsonObject.toJSONString());
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        deviceTagList.add(entry.getValue().replace(",", "&"));
                    }
                } catch (Exception e) {
                    deviceTagList.add("");
                    e.printStackTrace();
                }
            }
        } else {
            deviceTagList.add("");
        }

        String deviceTagString = StringUtils.join(deviceTagList, "|");
        tagList.add(deviceTagString);


        //============================================================================//

        String join = StringUtils.join(tagList, ",");

        return join;
    }

    /**
     * 从td获取本次访问的 X_Access_Token
     *
     * @param client
     * @param url
     * @return
     * @throws Exception
     */
    public static String getXAccessToken(CloseableHttpClient client, String url) throws Exception {
        //构造请求的url
        StringBuilder URLBuilder = new StringBuilder(url);
        URLBuilder.append("apikey=").append(apikey)
                .append("&apitoken=").append(apitoken);

        final HttpGet httpGet = new HttpGet(URLBuilder.toString());
        HttpResponse response = client.execute(httpGet);

        final int code = response.getStatusLine().getStatusCode();

        String X_Access_Token = "";

        if (code == 200) {
            String content = EntityUtils.toString(response.getEntity());
            final JSONObject jsonObject = JSON.parseObject(content);

            LOG.info("TDResponse=>" + jsonObject);

            final JSONObject data = (JSONObject) jsonObject.get("data");

            if ("true".equals(data.getString("login"))) {
                X_Access_Token = data.getString("token");
            }

            LOG.info("X-Access-Token=>" + X_Access_Token);
        } else {
            LOG.warn("获取本次访问的X-Access-Token失败:状态码=>" + code);
        }

        return X_Access_Token;
    }

    /**
     * 调用不同的接口获取数据
     *
     * @param client
     * @param url
     * @param type
     * @param id
     * @return
     * @throws Exception
     */
    public JSONArray getTagDataByTdUrl(CloseableHttpClient client, String url, String type, String id, String dbtype, String month) throws Exception {

        tdDataService.initRecode(id);

        final String info = tdDataService.getDataInfo(id, dbtype);

        if (StringUtils.isNotEmpty(info)) {
            return JSON.parseArray(info);
        }

        //构造请求的url
        StringBuilder URLBuilder = new StringBuilder(url);
        URLBuilder.append("apikey=").append(apikey)
                .append("&apitoken=").append(apitoken)
                .append("&id=").append(id)
                .append("&type=").append(type);

        //如果请求接口是城市信息查询的,请求参数增加
        if (url.contains("user-loc-city")) {
            URLBuilder.append("&month=").append(month);
        }

        BasicHeader[] headers = {new BasicHeader("Content-Type", "application/json;charset=utf-8"),
                new BasicHeader("X-Access-Token", X_Access_Token)
        };

        final HttpGet httpGet1 = new HttpGet(URLBuilder.toString());
        httpGet1.setHeaders(headers);

        final HttpResponse httpResponse = client.execute(httpGet1);
        final int statusCode = httpResponse.getStatusLine().getStatusCode();

        HttpEntity entity = httpResponse.getEntity();
        InputStream is = entity.getContent();

        byte b[] = new byte[10240];
        int len = 0;
        try {

            int temp = 0;                     //所有读取的内容都使用temp接收
            while ((temp = is.read()) != -1) {    //当没有读取完时，继续读取
                b[len] = (byte) temp;
                len++;
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String contents = new String(b, 0, len);

        /*InputStream content = httpResponse.getEntity().getContent();

        int available = content.available();
        byte[] b = new byte[available];
        int read = content.read(b, 0, available);

        String contents = new String(b);*/

        //String contents = EntityUtils.toString(httpResponse.getEntity(), Charset.forName("iso8859-1"));

        final JSONObject object = JSON.parseObject(contents);

        LOG.info("请求结果:" + object);

        JSONArray tag = null;

        if (statusCode == 200) {

            LOG.info("请求成功,开始解析结果数据...");

            if ("2001".equals(object.getString("code"))) {
                final JSONObject datas = (JSONObject) object.get("data");

                if (url.contains("user-loc-city")) {
                    tag = new JSONArray();
                    tag.add(datas);
                } else {
                    tag = datas.getJSONArray("tags");
                }
            }
        } else {

            if (statusCode == 401) {
                //获取本次访问权限认证(accessToken)的url
                String tdtoken = "https://api.talkingdata.com/tdmkaccount/authen/app/v2?";
                X_Access_Token = getXAccessToken(client, tdtoken);

                tag = getTagDataByTdUrl(client, url, type, id, dbtype, month);
            } else {
                LOG.warn("请求失败:状态码=>" + statusCode + ",msg=>" + object.getString("msg"));
            }
        }

        if (tag != null) {
            tdDataService.updateInfo(id, dbtype, tag.toJSONString());
        }

        return tag;
    }

    /**
     * 递归获取数据
     *
     * @param client
     * @param id
     * @param deep
     * @return
     */
    public JSONArray getCityDataByMonth(CloseableHttpClient client, String cityurl, String id, int deep) {

        if (deep > 3) {
            return null;
        }

        final String yyyyMM = getDateBerforMonths(deep);

        //常在城市信息查询接口
        JSONArray citytag = null;
        try {
            citytag = getTagDataByTdUrl(client, cityurl, "mac", id, "city_info", yyyyMM);
            //如果查询上月的数据为空,则查询上上个月
            if (CollectionUtils.isEmpty(citytag)) {
                citytag = getCityDataByMonth(client, cityurl, id, ++deep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return citytag;
    }

    /**
     * 获取当前月所在上一个月
     *
     * @param berfor
     * @return
     */
    public static String getDateBerforMonths(int berfor) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        final DateTime dateTime = new DateTime();
        return format.format(dateTime.minusMonths(berfor).getMillis());
    }

}
