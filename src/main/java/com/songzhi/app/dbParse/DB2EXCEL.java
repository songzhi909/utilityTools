package com.songzhi.app.dbParse;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.songzhi.utils.SpringContent;
import com.tianjian.security.bean.SecurityConfigMenus;
import com.tianjian.security.bean.SecurityConfigPublic;
import com.tianjian.security.bean.SecurityConfigPublicClass;


/**
 * 将数据库中的菜单导出成excel文件
 * @author songzhi
 *
 */
public class DB2EXCEL {

  public static void main(String[] args) throws Exception {
    
    Date startDate = new Date();
    Workbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet("平台菜单");
    int rowIndex = 0; //行数
    //标题 （序号、模块类别、模块、一级菜单、二级菜单、三级菜单）
    Row title = sheet.createRow(rowIndex++);
    title.createCell(0).setCellValue("序号");
    title.createCell(1).setCellValue("模块类别");
    title.createCell(2).setCellValue("模块");
    title.createCell(3).setCellValue("一级菜单");
    title.createCell(4).setCellValue("二级菜单");
    title.createCell(5).setCellValue("三级菜单");
    
    IMenuService service = SpringContent.getBean(IMenuService.class);
    List<SecurityConfigPublicClass> publicClasses = service.findSecurityConfigPublicClass("009");
    for(SecurityConfigPublicClass securityConfigPublicClass : publicClasses) {
      List<SecurityConfigPublic> securityConfigPublics = service.findSecurityConfigPublic(securityConfigPublicClass.getId());
      for(SecurityConfigPublic securityConfigPublic : securityConfigPublics) {
        List<SecurityConfigMenus> securityConfigMenusLevel1 = service.findSecurityConfigMenusByPublicId(securityConfigPublic.getId());
        for(SecurityConfigMenus menuLevel1 : securityConfigMenusLevel1) {
          List<SecurityConfigMenus> securityConfigMenusLevel2 = service.findSecurityConfigMenusByPid(menuLevel1.getId());
          
          if(securityConfigMenusLevel2.size()>0) {
            for(SecurityConfigMenus menuLevel2 : securityConfigMenusLevel2) {
              List<SecurityConfigMenus> securityConfigMenusLevel3 = service.findSecurityConfigMenusByPid(menuLevel2.getId());
              if(securityConfigMenusLevel3.size() > 0) {
                for(SecurityConfigMenus menuLevel3 : securityConfigMenusLevel3) {
                  rowIndex = createRow(sheet, rowIndex,
                      securityConfigPublicClass.getClassName(),
                      securityConfigPublic.getReason(),
                      menuLevel1.getMenuDetail(), 
                      menuLevel2.getMenuDetail(),
                      menuLevel3.getMenuDetail());
                }
              }else {
                rowIndex = createRow(sheet, rowIndex,
                    securityConfigPublicClass.getClassName(),
                    securityConfigPublic.getReason(), 
                    menuLevel1.getMenuDetail(), 
                    menuLevel2.getMenuDetail(),
                    "");
              }
            }
          }else { 
            rowIndex = createRow(sheet, rowIndex,
                securityConfigPublicClass.getClassName(),
                securityConfigPublic.getReason(), 
                menuLevel1.getMenuDetail(), "",""); 
          }
        }
      }
    }

    FileOutputStream fileout = new FileOutputStream("workbook.xls");
    wb.write(fileout);
    fileout.close();
    
    Date endDate = new Date();
    System.out.println((endDate.getTime()-startDate.getTime())/1000 + "秒");
    
  }

  private static int createRow(Sheet sheet, int rowIndex,
      String publicClassName, String publicName, String menuName1,String menuName2,String menuName3) {
    Row row = sheet.createRow(rowIndex);
    row.createCell(0).setCellValue(rowIndex);
    row.createCell(1).setCellValue(publicClassName);
    row.createCell(2).setCellValue(publicName);
    row.createCell(3).setCellValue(menuName1);
    row.createCell(4).setCellValue(menuName2);
    row.createCell(5).setCellValue(menuName3);
    rowIndex++;
    return rowIndex;
  }
  
}
