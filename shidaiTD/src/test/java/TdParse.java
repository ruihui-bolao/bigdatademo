import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.tracing.dtrace.ArgsAttributes;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/16 14:55
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TdParse {

    public static void main(String[] args) {

        String str = "{\"data\": {\"location\": [{\"province\": \"内蒙古自治区\",\"city\": \"呼和浩特市\"},{\"province\": \"内蒙古自治区\",\"city\": \"乌兰察布市\"},{\"province\": \"河北省\",\"city\": \"张家口市\"}],\"month\": \"201705\",\"tdid\": \"h906b16347c9f6f2f6ceae545166c2920\"}}";
        JSONObject jsonObject = (JSONObject) JSON.parse(str);
        String data = jsonObject.getString("data");
        JSONObject jsonObject1 = (JSONObject) JSON.parse(data);
        Object location = jsonObject1.get("location");
        final ArrayList<String> cityTagList = new ArrayList<String>();
        if ( location != null && location instanceof JSONArray){
            JSONArray array = (JSONArray) location;
            if (array.size() > 0){
                for (Object o : array) {
                    JSONObject object = (JSONObject) o;
                    cityTagList.add(object.getString("province") + object.getString("city"));
                }
            }else {
                cityTagList.add("");
            }
        }
        String cityTagListString = StringUtils.join(cityTagList, "|");
        System.out.println(cityTagListString);
    }
}
