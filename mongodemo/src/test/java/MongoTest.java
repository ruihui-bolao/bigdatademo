import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sssd on 2017/8/25.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class MongoTest {
    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test(){
        Query query = new Query();
        query.with(new Sort( new Sort.Order( Sort.Direction.DESC, "dwCreatedAt")));
        query.skip(0).limit(10);
        List<DBObject> test_ay = mongoTemplate.find(query, DBObject.class, "test_ay");
        for (DBObject dbObject:test_ay ){
            System.out.println(dbObject.toString());
        }
    }

}
