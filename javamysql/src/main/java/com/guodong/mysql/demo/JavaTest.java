package com.guodong.mysql.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

/**
 * Created by ninggd on 2017/11/17.
 *
 * 测试 java mysql 通过 读取配置文件中的sql查询语句，执行sql 查询命令
 */


public class JavaTest {

    protected static Logger LOG = LoggerFactory.getLogger(JavaTest.class);

    public static void main(String[] args) {

        // 待更新参数
        int taskTimeout = 20;
        // 加载配置文件
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContent.xml");
        // 获取jdbcTemplate
        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");
        //通过配置文件获取查询语句
        String queryStr = MyServerSql.getInstance().getProperty("jise.task.fail");
        // 执行MySQL查询语句
        int taskNumber = jdbcTemplate.update(queryStr, new Object[]{ taskTimeout });
        if (taskNumber > 0) {
            LOG.info("The Number of failed tasks is : " + taskNumber);
        }
    }

}
