package bailuyuan;

import bailuyuan.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/14 11:37
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:     两个 excel  之间的操作
 */
public class BaiLuYuan {

    public static String sTime = "2018-01-01 00:00:00";
    public static String eTime = "2018-03-04 23:59:59";

    /**
     * 从 excel 表格中提取时间(同一个mac 第一次出现的时间和最后一次出现的时间)
     *
     * @param path
     * @return
     */
    public static HashMap<String, String> extractTime(String path) {
        HashMap<String, String> macTimes = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
            XSSFSheet sheet = workbook.getSheetAt(0);
            macTimes = new HashMap<String, String>();
            String macLabel = "";
            String timeLabel = "";
            int lastRowNum = sheet.getLastRowNum();
            int i = 1;
            for (Row row : sheet) {
                System.out.println(++i);
                int rowNum = row.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                String phoneMac = row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue();
                double tempTime = row.getCell(2).getNumericCellValue();
                String timeTamp = new BigDecimal(tempTime).toPlainString();
//                String time = DateUtils.stampToDateStr(timeTamp);
                if (!macLabel.equalsIgnoreCase(phoneMac) || rowNum == lastRowNum) {
                    if (rowNum == 1) {
                        macTimes.put(phoneMac, timeTamp);
                    } else {
                        if (!macTimes.containsKey(phoneMac)) {
                            macTimes.put(phoneMac, timeTamp);
                        }
                        String tempStr = macTimes.get(macLabel);
                        if (rowNum == lastRowNum) {
                            macTimes.put(macLabel, tempStr + "," + timeTamp);
                        } else {
                            macTimes.put(macLabel, tempStr + "," + timeLabel);
                        }
                    }
                }
                macLabel = phoneMac;
                timeLabel = timeTamp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macTimes;
    }


    /**
     * 读取 excel 中的文件内容
     *
     * @param path 原始数据的存放路径
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
                try {
                    ArrayList<String> rowList = new ArrayList<String>();
                    StringBuffer sb = new StringBuffer();
                    String wifiMac = row.getCell(4) == null ? "" : row.getCell(4).getStringCellValue();
                    String siteName = row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue();
                    String phoneMac = row.getCell(8) == null ? "" : row.getCell(8).getStringCellValue();
                    rowList.add(phoneMac);
                    sb.append(wifiMac + ",").append(siteName + ",").append(phoneMac + ",");
                    rowList.add(sb.toString());
                    resList.add(rowList);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    /**
     * 比较时间在不在规定时间范围内
     *
     * @param startTime
     * @return
     */
    public static boolean compare(Long startTime) {
        try {
            long stamp = DateUtils.dateToStamp2(sTime);
            long etamp = DateUtils.dateToStamp2(eTime);
            if (startTime >= (stamp / 1000) && (etamp / 1000) >= startTime) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 处理文件
     *
     * @param sourcePath 原数据文件路径
     * @param dataPath   临时保存文件路径
     * @return
     */
    public static Boolean handleFile(String sourcePath, String dataPath) {
        try {
            ArrayList<ArrayList<String>> resList = BaiLuYuan.readFromExcel(sourcePath);
            HashMap<String, String> resMap = BaiLuYuan.extractTime(sourcePath);
            File dataFile = new File(dataPath);
            int i = 0;
            for (ArrayList<String> list : resList) {
                System.out.printf("开始处理第%d条数据", ++i);
                String phoneMac = list.get(0);
                String timeStr = resMap.get(phoneMac);
                String[] split = timeStr.split(",");
                if (split.length == 1) {
                    timeStr = timeStr + "," + timeStr;
                }
                Long startTime = Long.valueOf(split[0]);
                boolean compare = compare(startTime);
                if (compare) {
                    String[] split1 = timeStr.split(",");
                    String time1 = DateUtils.stampToDateStr(split1[0]);
                    String time2 = DateUtils.stampToDateStr(split1[1]);
                    String allTime = time1 + "," + time2;
                    String resStr = list.get(1) + allTime + "\n";
                    FileUtils.writeStringToFile(dataFile, resStr, "gbk", true);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用来比较两短时间相差几天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Long compareDate(String startTime, String endTime) throws Exception {
        Date old = new Date();
        Date news = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        old = sdf.parse(startTime);
        news = sdf.parse(endTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(old);
        long time1 = cal.getTimeInMillis();
        cal.setTime(news);
        long time2 = cal.getTimeInMillis();
        long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
        return betweenDays;
    }

    /**
     * 对文件中的数据进行去重
     *
     * @param saveTempPath 临时保存的文件路径
     * @param uniquePath   去重后文件路径
     * @return
     */
    public static boolean removal(String saveTempPath, String uniquePath) {
        try {
            List<String> lines = FileUtils.readLines(new File(saveTempPath), "gbk");
            HashSet<String> set = new HashSet<String>();
            HashMap<String, Long> mapCount = new HashMap<String, Long>();
            for (String line : lines) {
                String[] split = line.split(",");
                String phoneMac = split[2];
                // 统计 mac 出现的次数
                if (mapCount.containsKey(phoneMac)) {
                    Long aLong = mapCount.get(phoneMac);
                    mapCount.put(phoneMac, aLong + 1L);
                } else {
                    mapCount.put(phoneMac, 1L);
                }

                // 计算时间相差的天数
                String startTime = split[3];
                String endTime = split[4];
                Long date = compareDate(startTime, endTime);

                if (mapCount.get(phoneMac) != null && mapCount.get(phoneMac) > 2 && date < 7) {
                    set.add(phoneMac);
                }
            }

            for (String line : lines) {
                String[] split = line.split(",");
                String phoneMac = split[2];
                if (set.contains(phoneMac)) {
                    String str = line + "\n";
                    FileUtils.writeStringToFile(new File(uniquePath), str, "UTF-8", true);
                    set.remove(phoneMac);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) throws Exception {

        // 从 excel 中读取数据 并临时保存(ssdb数据导出后保存文件)
        String sourcePath = "C:\\Users\\sssd\\Desktop\\bailuyuan\\bailuyuan_3.xlsx";
        // 按要求整理数据格式
        String saveTempPath = "C:\\Users\\sssd\\Desktop\\bailuyuan\\temp_3.csv";
        // 将数据去重后用来调用TD接口
        String uniquePath = "C:\\Users\\sssd\\Desktop\\bailuyuan\\uniqueTemp.txt";

/*        // 合并文件
        Boolean aBoolean = BaiLuYuan.handleFile(sourcePath, saveTempPath);
        System.out.println(aBoolean);

        // 临时文件去重
        boolean removal = removal(saveTempPath, uniquePath);
        System.out.println(removal);*/


        // 调用Td接口，用来保存结果的数据
      /*  File resFile = new File("C:\\Users\\sssd\\Desktop\\bailuyuan\\res.csv");
        //获取本次访问权限认证(accessToken)的url
        CloseableHttpClient client = HttpUtils.getHttpClient();
        final String tdtoken = "https://api.talkingdata.com/tdmkaccount/authen/app/v2?";
        final String X_Access_Token = TDCall.getXAccessToken(client, tdtoken);
        final TDCall tdCall = new TDCall(X_Access_Token);
        List<String> lines = FileUtils.readLines(new File(uniquePath), "UTF-8");
        int i = 1;
        for (String line : lines) {
            System.out.printf("开始第%d条数据%n",i++);
            List<String> lineList = Arrays.asList(line.split(","));
            String phoneMac = lineList.get(2);
            //  TD 接口调用
            final String join = tdCall.parserData(client, phoneMac);
            if (StringUtils.isNotBlank(join)) {
                final String res = line + "," + join + "\n";
                //解析完成后写出的结果文件csv
                FileUtils.writeStringToFile(resFile, res, "gbk", true);
            } else {
                final String res = line + "," + ",,,,,," + "\n";
                //解析完成后写出的结果文件csv
                FileUtils.writeStringToFile(resFile, res, "gbk", true);
            }
        }*/

    }

}
