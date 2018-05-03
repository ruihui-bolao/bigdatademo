package excelfile;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/4/10 10:11
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    读取excel文件，并动态添加指定的 excel 中
 */
public class ReadAndSavaExcel {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    private static final String NEG = "C:\\Users\\sssd\\Desktop\\data\\neg.xlsx";
    private static final String POS = "C:\\Users\\sssd\\Desktop\\data\\pos.xlsx";
    private static final String MID = "C:\\Users\\sssd\\Desktop\\data\\mide.xlsx";

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
     * 遍历制定目录中的所有文件
     *
     * @param filePath
     */
    public static void eachFile(String filePath) throws Exception {

        File dicFile = new File(filePath);
        File[] files = dicFile.listFiles();
        for (File file : files) {
            Workbook workbook1 = getWorkbok(file);
            // 默认获取第一个sheet
            Sheet sheet1 = workbook1.getSheetAt(0);
            for (Row row1 : sheet1) {
                int rowNum = row1.getRowNum();
                if (rowNum == 0) {
                    continue;
                }
                double cellValue = row1.getCell(1).getNumericCellValue();
                Workbook workbook2 = null;
                FileOutputStream out = null;
                if (cellValue == 0) {
                    workbook2 = getWorkbok(new File(NEG));
                    out = new FileOutputStream(NEG);
                } else if (cellValue == 1) {
                    workbook2 = getWorkbok(new File(MID));
                    out = new FileOutputStream(MID);
                } else {
                    workbook2 = getWorkbok(new File(POS));
                    out = new FileOutputStream(POS);
                }
                Sheet sheet2 = workbook2.getSheetAt(0);
                Row newRow = sheet2.createRow((short) (sheet2.getLastRowNum() + 1));
                newRow.createCell(0).setCellValue(row1.getCell(0).getStringCellValue());
                out.flush();
                workbook2.write(out);
                out.close();
            }

        }
    }

    public static void main(String[] args) throws Exception {
        String inputPath = "C:\\Users\\sssd\\Desktop\\情感帖子";
        eachFile(inputPath);
    }

}
