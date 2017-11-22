import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.api.java.UDF1;
import utils.IpUtils;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/22 9:30
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    spark 自定义函数(需实现 spark 的 udf 函数)
 */
public class IP2LongUDF implements UDF1<String, Long> {
    @Override
    public Long call(String ip) throws Exception {
        if(StringUtils.isNotBlank(ip)){
            return IpUtils.ipv4ToLong(ip);
        }else return 0l;
    }
}
