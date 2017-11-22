package com.guodong.mysql.demo;

import org.junit.Test;

public class PropertyUtilTest {
    @Test
    public void test() {
        String tree = PropertyUtil.getTree(5);
        System.out.println(tree);
    }
}