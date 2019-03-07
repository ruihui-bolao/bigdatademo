package dubbo.entity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/8/21 16:47
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Student implements Serializable {

    public String name;
    public String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
