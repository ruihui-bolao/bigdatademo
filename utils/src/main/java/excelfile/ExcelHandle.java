package excelfile;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hui
 * Date: 2018/4/8 21:01
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   用来处理 excel 表格
 */
public class ExcelHandle {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    private static Sheet sheet = null;

    /**
     * 判断excel的版本，获取Workbook
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        Workbook wb = null;
        if (file.getName().endsWith(EXCEL_XLS)) {     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(EXCEL_XLSX)) {    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 获取excel文件的列名
     *
     * @param filePath excel 的文件路径
     * @return
     * @throws Exception
     */
    public static HashMap<String, Integer> getHeader(String filePath) throws Exception {
        Workbook workbok = getWorkbok(new File(filePath));
        // 获取excel工作页，默认只有一个工作页
        sheet = workbok.getSheetAt(0);
        // 获取标签页
        Row row = sheet.getRow(0);
        int numberOfCells = row.getPhysicalNumberOfCells();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < numberOfCells; i++) {
            String temp = row.getCell(i) == null ? "" : row.getCell(i).getStringCellValue();
            map.put(temp, i);
        }
        return map;
    }

    /**
     * 找到 excel 中指定列相同的行数。
     *
     * @param maskMap
     * @return
     */
    public HashMap<String, Set<Integer>> findSame(String inputPath, HashMap<String, Integer> maskMap) throws Exception {
        HashMap<String, Set<Integer>> resMap = new HashMap<String, Set<Integer>>();
        Collection<Integer> values = maskMap.values();
        if (StringUtils.isBlank(inputPath)) {
            sameExecutor(resMap, values);
        } else {
            Workbook workbok = getWorkbok(new File(inputPath));
            // 获取excel工作页，默认只有一个工作页
            sheet = workbok.getSheetAt(0);
        }
        return resMap;
    }

    /**
     *  查找雷同的行数，其中 key 为 字符串， value 是出现雷同的行数
     * @param resMap
     * @param values
     */
    private void sameExecutor(HashMap<String, Set<Integer>> resMap, Collection<Integer> values) {
        int i = 1;
        for (Row row : sheet) {
            System.out.println(++i);
            int rowNum = row.getRowNum();
            if (rowNum == 0) {
                continue;
            }
            StringBuffer buffer = new StringBuffer();
            for (Integer index : values) {
                String temp = row.getCell(index) == null ? "" : row.getCell(index).getStringCellValue();
                buffer.append(temp).append("-");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            if (resMap.containsKey(buffer.toString())) {
                Set<Integer> set = resMap.get(buffer.toString());
                set.add(i);
            } else {
                HashSet<Integer> indexSet = new HashSet<Integer>();
                indexSet.add(i);
                resMap.put(buffer.toString(), indexSet);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        String path = "C:\\Users\\hui\\Desktop\\test.xlsx";
        HashMap<String, Integer> header = ExcelHandle.getHeader(path);
        for (Map.Entry<String, Integer> entry : header.entrySet()) {
            System.out.println(entry.getKey() + "----" + entry.getValue());
        }
    }
}
