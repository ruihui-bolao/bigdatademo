package com.sdyc.bean;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/16 10:42
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Parms {
    private Integer id;
    private String value;

    @Override
    public String toString() {
        return "Parms{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setValue(String name) {
        this.value = name;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

}
