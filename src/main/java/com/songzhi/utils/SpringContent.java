package com.songzhi.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring 容器
 * @author songz
 *
 */
public class SpringContent {
  
  private static ApplicationContext context;
  
  static {
    context = new ClassPathXmlApplicationContext("applicationContext.xml");
  }
  
  public static Object getBean(String name) {
    return context.getBean(name);
  }
  
  public static <T> T getBean(Class<T> requiredType) {
    return context.getBean(requiredType);
  }
  
  public static ApplicationContext instance() {
    return context;
  }

}
