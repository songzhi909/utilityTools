package com.songzhi.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * 配置文件读取
 * @author songz
 *
 */
public class PropertiesUtil {
  private static HashMap<String, String> propertys;

  static {
    try {
      Properties p = new Properties();
      p.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("comm.properties"));
      String key;
      String value;
      HashMap<String, String> map = new HashMap<String, String>();
      for (Enumeration<?> enu = p.propertyNames(); enu.hasMoreElements(); map.put(key, value)) {
        key = (String) enu.nextElement();
        value = p.getProperty(key);
      }
      propertys = map;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getProperty(String key) {
    String str = "";
    if (propertys != null) {
      if (propertys.containsKey(key)) {
        str = (String) propertys.get(key);
      }
    }
    return str;
  }
}
