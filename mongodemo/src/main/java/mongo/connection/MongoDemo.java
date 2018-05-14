package mongo.connection;

import com.mongodb.DBObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sssd on 2017/8/25.
 * <p>
 * 通过MongoTemplate 连接Mongo
 */


public class MongoDemo {
    /**
     * mongoTemplate 服务
     */
    protected static MongoTemplate mongoTemplate;

    /**
     * 构造器初始化，
     */
    public MongoDemo() {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        BeanFactory factory = (BeanFactory) context;
        mongoTemplate = (MongoTemplate) factory.getBean("mongoTemplate");
    }

    public static void main(String[] args) {

        mongo.connection.MongoDemo mongoDemo = new mongo.connection.MongoDemo();
        Query query = new Query();
        query.with(new Sort( new Sort.Order( Sort.Direction.DESC, "dwCreatedAt")));
        query.skip(0).limit(10);
        List<DBObject> test_ay = mongoDemo.mongoTemplate.find(query, DBObject.class, "currDay2");
        for (DBObject dbObject:test_ay ){
            System.out.println(dbObject.toString());
        }

        /*//按照时间段查询
        Date endDate = new Date();
        // Java Calendar 日历，抽象定义了足够的方法，可以表述日历的规则。
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        // 以当前时间往前推两天
        calendar.add(Calendar.DATE, -2);
        Date startDate = calendar.getTime();

        //设置查询条件
        MongoDemo mongoDemo = new MongoDemo();
        // mongo 为查询添加查询准则
        Query query1 = new Query().addCriteria(Criteria.where("dwCreatedAt").gt(startDate).lte(endDate));
        System.out.println("查询条件为： " + query1.toString());
        // Mongo 执行查询语句
        List<DBObject> dbObjects = mongoTemplate.find(query1, DBObject.class, "test_ax");
        for (DBObject dbObject : dbObjects) {
            System.out.println(dbObject.toString());
        }*/

    }
}
