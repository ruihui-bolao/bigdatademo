package sdyc.bailuyuan;

import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sdyc.bailuyuan.util.DateUtils;
import sdyc.bailuyuan.util.HttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/14 11:37
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:     两个 excel  之间的操作
 */
public class BaiLuYuan {


    /**
     * 从 excel 表格中提取时间
     *
     * @param path
     * @return
     */
    public HashMap<String,String> extractTime(String path){
        String path = "C:\\Users\\sssd\\Desktop\\bailuyuan\\test1.xlsx";
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
            XSSFSheet sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 读取 excel 中的文件内容
     *
     * @param path  原始数据的存放路径
     * @return
     */
    public static ArrayList<ArrayList<String>> readFromExcel(String path) {
        ArrayList<ArrayList<String>> resList = new ArrayList<ArrayList<String>>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                ArrayList<String> rowList = new ArrayList<String>();
                StringBuffer sb = new StringBuffer();
                String wifiMac = row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue();
                String siteName = row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue();
                String phoneMac = row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue();
                double startTemp = row.getCell(2).getNumericCellValue();
                String startTamp = new BigDecimal(startTemp).toPlainString();
                String startTime = DateUtils.stampToDateStr(startTamp);
                double endTemp = row.getCell(7).getNumericCellValue();
                String endTamp = new BigDecimal(endTemp).toPlainString();
                String endTime = DateUtils.stampToDateStr(endTamp);
                rowList.add(phoneMac);
                sb.append(wifiMac + ",").append(siteName + ",").append(phoneMac + ",").append(startTime + ",").append(endTime + ",");
                rowList.add(sb.toString());
                resList.add(rowList);
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }



    public static void main(String[] args) throws Exception {

        // 从元数据 excel 中读取数据
        String path = "C:\\Users\\sssd\\Desktop\\bailuyuan\\test1.xlsx";
        ArrayList<ArrayList<String>> resList = BaiLuYuan.readFromExcel(path);

        CloseableHttpClient client = HttpUtils.getHttpClient();
        //获取本次访问权限认证(accessToken)的url
        final String tdtoken = "https://api.talkingdata.com/tdmkaccount/authen/app/v2?";
        final String X_Access_Token = TDCall.getXAccessToken(client, tdtoken);
        final TDCall tdCall = new TDCall(X_Access_Token);

        if (resList != null && resList.size() > 0){
            for (ArrayList<String> rowList : resList) {
                String mac = rowList.get(0);
                final String join = tdCall.parserData(client, mac);
                File file = new File("C:\\Users\\sssd\\Desktop\\bailuyuan\\result.csv");

                String res = rowList.get(1) + "\n";
                FileUtils.writeStringToFile(file, res, "gbk", true);
            }
        }

    }

}
