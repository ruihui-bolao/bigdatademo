package com.guodong.mysql.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

/**
 * Created by ninggd on 2017/11/17.
 */


public class JavaTest {



    public static void main(String[] args) {

        int taskTimeout = 20;

        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContent.xml");
        JdbcTemplate jdbcTemplate = (JdbcTemplate) context.getBean("jdbcTemplate");

        String queryStr = MyServerSql.getInstance().getProperty("jise.task.fail");

        int taskNumber = jdbcTemplate.update(queryStr, new Object[]{ taskTimeout });


    }

}
