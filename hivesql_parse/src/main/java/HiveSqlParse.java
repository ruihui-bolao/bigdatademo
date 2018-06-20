import org.antlr.runtime.tree.Tree;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.Context;
import org.apache.hadoop.hive.ql.parse.ParseException;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/6/15 16:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   尝试利用hivesql 解析sql语句。
 */
public class HiveSqlParse {


    /**
     * hive 解析sql语句返回的是树
     *
     * @param sqlStr
     * @return
     */
    public ASTNode hiveParseSql(String sqlStr) {
        ASTNode node = null;
        try {
            ParseDriver pd = new ParseDriver();
            node = pd.parse(sqlStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return node;
    }

    /**
     * 对hive解析的树进行解析
     *
     * @param node
     */
    public String parseTree(ASTNode node) {
        int childCount = node.getChildCount();
        // 解析返回的结果
        String resStr = null;
        //  用来判断是否有空
        if (childCount != 0) {
            for (int i = 0; i < childCount; i++) {
                Tree child = node.getChild(i);
                if (child.getChildCount() == 0 && child.getTokenStopIndex() != -1) {
                    System.out.println(child.getText());
                    return child.getText();
                }
                resStr = parseTree((ASTNode) child);
                if (StringUtils.isNotBlank(resStr)) {
                    System.out.println(child.getText());
                }
            }
        }
        return resStr;
    }

    public static void main(String[] args) throws Exception {
        String sql = "select * from table1 where a > 100";
        HiveSqlParse hiveSqlParse = new HiveSqlParse();
        ASTNode node = hiveSqlParse.hiveParseSql(sql);
        hiveSqlParse.parseTree(node);
    }

}
