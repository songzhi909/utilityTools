package com.songzhi.app.dbParse;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import com.songzhi.utils.SpringContent;


/**
 * 将数据库的菜单数据转换为xsd文件
 * @author songzhi
 *
 */
public class DB2XSD {

  public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
    
    IMenuService menuService = SpringContent.getBean(IMenuService.class);
    menuService.generate("cehrp.xsd", "009");
    
  }
  
}
