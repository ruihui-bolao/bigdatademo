import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.Context;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/6/15 16:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   尝试利用hivesql 解析sql语句。
 */
public class HiveSqlParse {

    public static void main(String[] args) throws Exception {
        String sql = "select * from table1 where a > 100";
//        Context context = new Context(new HiveConf());
        ParseDriver pd = new ParseDriver();
        ASTNode node = pd.parse(sql);
        System.out.println(node);
    }

}
