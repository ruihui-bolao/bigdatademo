import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/26 15:10
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   java 通过 rpc 调用 python 接口
 */
public class JavaPython {

    public static void main(String[] args) throws Exception {

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://192.168.102.16:8888/RPC2"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        double a = 0.6;
        Object[] objects = {a};
        Double res = (Double)client.execute("addNum", objects);
        System.out.println(res);

    }

}
