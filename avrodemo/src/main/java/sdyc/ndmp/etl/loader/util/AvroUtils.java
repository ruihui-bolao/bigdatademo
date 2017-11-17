package sdyc.ndmp.etl.loader.util;

import org.apache.avro.Schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/27 10:00
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class AvroUtils {

    /**
     * 用来存放所有的schema
     */
    private static Map<String, Schema> schemas = new HashMap<String, Schema>();

    private AvroUtils() {
    }

    /**
     * 向schemas中添加schema
     *
     * @param name
     * @param schema
     */
    public static void addSchema(String name, Schema schema) {
        schemas.put(name, schema);
    }

    /**
     * 根据name获取schema
     *
     * @param name
     * @return
     */
    public static Schema getShcema(String name) {
        return schemas.get(name);
    }

    /**
     * 用来解析schema
     *
     * @param sc
     * @return
     */
    public static String resolveSchema(String sc) {
        String result = sc;
        for (Map.Entry<String, Schema> entry : schemas.entrySet())
            result = replace(result, entry.getKey(),
                    entry.getValue().toString());
        return result;
    }

    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    public static Schema parseSchema(String schemaString) {
        String completeSchema = resolveSchema(schemaString);
        Schema schema = new Schema.Parser().parse(completeSchema);
        String name = schema.getFullName();
        schemas.put(name, schema);
        return schema;
    }

    public static Schema parseSchema(InputStream in) throws IOException {
        StringBuffer buffer = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            buffer.append(new String(b, 0, n));
        }
        return parseSchema(buffer.toString());
    }

    public static Schema parseSchema(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return parseSchema(fis);
    }

}
