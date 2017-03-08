package com.songzhi.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Excel工具类
 * @author songz
 *
 */
public class ExcelUtil {

  /**
   * 生成Excel文件
   * 
   * @param caption
   *          主标题
   * @param list
   *          数据，每个数据中的长度应与列标题长度一致
   * @param columnArray
   *          列标题 <a style='color:red'>notice: 支持双行,标题与子标题间用&分割</a>
   * @return
   */
  public static HSSFWorkbook generateExcel(String caption, List<?> list, Object[] columnArray) {

    // 创建对工作表的引用
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("sheet1");
    sheet.setColumnWidth(0, 3000);

    HSSFRow row = null;
    HSSFCell cell = null;
    short rn = 0; // 行数
    short totalCn = 0; // 总列数

    // 定义表格样式
    HSSFFont font = workbook.createFont();
    font.setColor(HSSFFont.COLOR_NORMAL);
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

    HSSFCellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

    HSSFCellStyle HeaderStyle = workbook.createCellStyle();

    HeaderStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    HeaderStyle.setFont(font);
    HeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
    HeaderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    HeaderStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);
    HeaderStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);
    HeaderStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);

    HSSFCellStyle nullStyle = workbook.createCellStyle();
    nullStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

    nullStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
    nullStyle.setBorderBottom(HSSFCellStyle.BORDER_NONE);
    ;
    nullStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);
    nullStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);
    nullStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);

    // 插入主标题
    row = sheet.createRow(rn++);
    row.setHeightInPoints((float) 30); // 设置行高-像素
    cell = row.createCell(0);
    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    cell.setCellStyle(HeaderStyle);
    cell.setCellValue(caption);

    // 检查是否双行标题
    boolean single = true;
    for (int i = 0; i < columnArray.length && single; i++)
      single = columnArray[i].toString().indexOf("&") == -1;

    short cn = 0; // 列数,每次创建新的行的重置列数
    // 插入列标题
    if (single) { //
      row = sheet.createRow(rn++);
      for (Object col : columnArray)
        createCell(row, cellStyle, cn++, col.toString());
    } else {
      String[] firstCols = new String[columnArray.length]; // 一级列标题
      int[] firstColsNum = new int[columnArray.length]; // 一级列标题 占用列数
      List<String> tempList = new ArrayList<String>(); // 二级列标题
      for (int i = 0; i < columnArray.length; i++) {
        String tempStr = columnArray[i].toString();
        if (tempStr.indexOf("&") != -1) {
          String[] arrs = tempStr.split("&");
          firstCols[i] = arrs[0];
          firstColsNum[i] = arrs.length - 1;
          for (int j = 1; j < arrs.length; j++) {
            tempList.add(arrs[j]);
          }
        } else {
          firstCols[i] = tempStr;
          firstColsNum[i] = 1;
          tempList.add(tempStr);
        }
      }

      row = sheet.createRow(rn++);// 插入一级列标题
      for (int i = 0; i < firstCols.length; cn += firstColsNum[i] - 1, i++)
        createCell(row, cellStyle, cn++, firstCols[i]);

      row = sheet.createRow(rn++);
      cn = 0; // 插入二级列标题
      for (int i = 0; i < tempList.size(); i++)
        createCell(row, cellStyle, cn++, tempList.get(i));

      short cnIndex = 0;
      for (int i = 0; i < firstCols.length; i++) {// 合并一级标题
        if (firstColsNum[i] == 1) {
          // public Region(int rowFrom, short colFrom, int rowTo, short colTo)
          sheet.addMergedRegion(new CellRangeAddress(1, cnIndex, 2, cnIndex));
        } else {
          sheet.addMergedRegion(new CellRangeAddress(1, cnIndex, 1, (short) (cnIndex + firstColsNum[i] - 1)));
        }
        cnIndex += firstColsNum[i];
      }
      // sheet.addMergedRegion(new Region(0,(short)0,0,totalCn));

    }

    totalCn = (short) (cn - 1);
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalCn)); // 合并主标题

    for (int i = 0; i < list.size(); i++) {
      Object[] objs = (Object[]) list.get(i);
      row = sheet.createRow(rn++);
      for (int j = 0; j < totalCn + 1; j++) { // 只加载跟列标题相同的列数
        createCell(row, cellStyle, (short) j, objs[j].toString());
      }

    }
    return workbook;
  }

  /**
   * 生成Excel文件 带序号
   * 
   * @param caption
   *          主标题
   * @param list
   *          数据，每个数据中的长度应与列标题长度一致
   * @param columnArray
   *          列标题
   * @return
   */
  public static HSSFWorkbook generateExcelWithSeq(String caption, List<?> list, Object[] columnArray) {

    // 创建对工作表的引用
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet("sheet1");
    sheet.setColumnWidth(0, 3000);

    HSSFRow row = null;
    HSSFCell cell = null;
    short rn = 0; // 行数
    short totalCn = 0; // 总列数

    // 定义表格样式
    HSSFFont font = workbook.createFont();
    font.setColor(HSSFFont.COLOR_NORMAL);
    font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

    HSSFCellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

    HSSFCellStyle HeaderStyle = workbook.createCellStyle();

    HeaderStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    HeaderStyle.setFont(font);
    HeaderStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
    HeaderStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    HeaderStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);
    HeaderStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);
    HeaderStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);

    HSSFCellStyle nullStyle = workbook.createCellStyle();
    nullStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

    nullStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
    nullStyle.setBorderBottom(HSSFCellStyle.BORDER_NONE);
    ;
    nullStyle.setBorderLeft(HSSFCellStyle.BORDER_NONE);
    nullStyle.setBorderRight(HSSFCellStyle.BORDER_NONE);
    nullStyle.setBorderTop(HSSFCellStyle.BORDER_NONE);

    // 插入标题

    row = sheet.createRow(rn++);
    row.setHeightInPoints((float) 30); // 设置行高-像素
    cell = row.createCell(0);
    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    cell.setCellStyle(HeaderStyle);
    cell.setCellValue(caption);

    // 插入列标题
    row = sheet.createRow(rn++);
    short cn = 0; // 列数,每次创建新的行的重置列数
    createCell(row, cellStyle, cn++, "序号");
    for (Object col : columnArray) {
      createCell(row, cellStyle, cn++, col.toString());
    }

    totalCn = (short) (cn - 1);
    sheet.addMergedRegion(new CellRangeAddress(0, (short) 0, 0, totalCn)); // 合并标题

    int seq = 0;// 序号,随机构类型递增

    for (int i = 0; i < list.size(); i++) {
      Object[] objs = (Object[]) list.get(i);
      row = sheet.createRow(rn++);
      if (i == list.size() - 1) {// 合计
        int num = 0;
        for (int n = 0; n < objs.length && objs[0].equals(objs[n]); n++)
          num++; // 算出合并多少列
        createCell(row, cellStyle, (short) 0, objs[0].toString()); // 最后一行为合计
        for (int j = num; j <= columnArray.length; j++) {
          createCell(row, cellStyle, (short) j, objs[j - 1].toString());
        }
        sheet.addMergedRegion(new CellRangeAddress(rn - 1, (short) 0, rn - 1, (short) num)); // 合并标题
      } else {
        createCell(row, cellStyle, (short) 0, ++seq + "");
        for (int j = 1; j <= columnArray.length; j++) {
          createCell(row, cellStyle, (short) j, objs[j - 1].toString());

        }
      }

    }

    return workbook;
  }

  /** 创建单元格 */
  private static HSSFCell createCell(HSSFRow row, HSSFCellStyle style, short cellNum, String text) {
    HSSFCell cell;
    cell = row.createCell((int) cellNum++);
    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    // cell.setEncoding(HSSFCell.ENCODING_UTF_16);
    cell.setCellStyle(style);
    cell.setCellValue(text);
    return cell;
  }

  /**
   * 读取excel文件
   * 
   * @param filepath
   *          文件路径
   * @param offset
   *          行数偏移量
   * @return
   */
  public static List<?> readExcelFile(String filepath, int offset) throws Exception {
    List<Object[]> list = new ArrayList<Object[]>();
    // Create the input stream from the xlsx/xls file
    FileInputStream fis = new FileInputStream(filepath);

    // Create Workbook instance for xlsx/xls file input stream
    Workbook workbook = null;
    if (filepath.toLowerCase().endsWith("xlsx")) {
      // workbook = new XSSFWorkbook(fis);
    } else if (filepath.toLowerCase().endsWith("xls")) {
      workbook = new HSSFWorkbook(fis);
    }

    Sheet sheet = workbook.getSheetAt(0); // 默认只选第一个sheet

    int rowNum = sheet.getPhysicalNumberOfRows(); // 行数
    for (int j = offset; j < rowNum; j++) {

      // Get the row object
      Row row = sheet.getRow(j);

      int colsNum = row.getPhysicalNumberOfCells();// 列数
      Object[] objs = new Object[colsNum];

      for (int i = 0; i < colsNum; i++) {
        Cell cell = row.getCell(i);
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
          objs[i] = cell.getStringCellValue().trim();
          break;
        case Cell.CELL_TYPE_NUMERIC:
          objs[i] = cell.getNumericCellValue();
          break;
        case Cell.CELL_TYPE_BOOLEAN:
          objs[i] = cell.getBooleanCellValue();
          break;
        case Cell.CELL_TYPE_BLANK:
          objs[i] = "";
          break;
        }
      }

      list.add(objs);

    }

    return list;
  }

  /**
   * 读取excel文件
   * 
   * @param filepath
   * @return
   */
  public static List<?> readExcelFile(String filepath) throws Exception {
    return readExcelFile(filepath, 0);
  }

}
