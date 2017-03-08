package com.songzhi.app.dbParse;

import java.util.List;

import com.tianjian.security.bean.SecurityConfigMenus;
import com.tianjian.security.bean.SecurityConfigPublic;
import com.tianjian.security.bean.SecurityConfigPublicClass;

public interface IMenuService {

  /** 根据租户ID查询模块类别 */
  List<SecurityConfigPublicClass> findSecurityConfigPublicClass(String tenantId);

  /** 根据上级模块类别ID查询下级模块类别 */
  List<SecurityConfigPublicClass> findSecurityConfigPublicClassByParentId(String parentId);

  /** 根据模块类别ID查询模块 */
  List<SecurityConfigPublic> findSecurityConfigPublic(String scpcId);

  /** 根据模块ID查询菜单 */
  List<SecurityConfigMenus> findSecurityConfigMenusByPublicId(String publicId);

  /** 查询菜单列表 */
  List<SecurityConfigMenus> findSecurityConfigMenusByLevel(String level);

  /** 根据上级菜单ID查询下级菜单列表 */
  List<SecurityConfigMenus> findSecurityConfigMenusByPid(String parentId);

  /**
   * 生成菜单数据的xsd文件
   */
  void generate(String path, String tenantId);

}