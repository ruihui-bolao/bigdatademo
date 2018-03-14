package sdyc.bailuyuan.service;

import java.io.Serializable;


public interface TDDataService extends Serializable{
    public boolean initRecode(String mac) ;
    public String getDataInfo(String mac, String type);
    public Integer  updateInfo(String mac, String type, String value);
}
