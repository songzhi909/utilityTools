package com.songzhi.app.dbParse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.songzhi.utils.PropertiesUtil;
import com.tianjian.security.bean.SecurityConfigMenus;
import com.tianjian.security.bean.SecurityConfigPublic;
import com.tianjian.security.bean.SecurityConfigPublicClass;
import com.tianjian.security.dao.ISecurityConfigMenusDAO;
import com.tianjian.security.dao.ISecurityConfigPublicClassDAO;
import com.tianjian.security.dao.ISecurityConfigPublicDAO;

@Service
@Transactional
public class MenuService implements IMenuService {

  @Autowired
  private ISecurityConfigPublicClassDAO securityConfigPublicClassDAO;
  @Autowired
  private ISecurityConfigPublicDAO securityConfigPublicDAO;
  @Autowired
  private ISecurityConfigMenusDAO securityConfigMenusDAO;

  /*
   * (non-Javadoc)
   * 
   * @see com.songzhi.app.dbParse.IMenuService#findSecurityConfigPublicClass()
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SecurityConfigPublicClass> findSecurityConfigPublicClass(String tenantId) {
    List<?> list = this.securityConfigPublicClassDAO.find("select t from SecurityConfigPublicClass t where t.parentId is null and t.tenantId = ? order by t.serialNo asc", tenantId);
    return (List<SecurityConfigPublicClass>) list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.songzhi.app.dbParse.IMenuService#
   * findSecurityConfigPublicClassByParentId(java.lang.String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SecurityConfigPublicClass> findSecurityConfigPublicClassByParentId(String parentId) {
    List<?> list = this.securityConfigPublicClassDAO.find("select t from SecurityConfigPublicClass t where t.parentId = ?  order by t.serialNo asc", parentId);
    return (List<SecurityConfigPublicClass>) list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.songzhi.app.dbParse.IMenuService#findSecurityConfigPublic(java.lang.
   * String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SecurityConfigPublic> findSecurityConfigPublic(String scpcId) {
    String hql = "select t from SecurityConfigPublic t where t.scpcId = ? order by id asc";
    List<?> list = this.securityConfigPublicDAO.find(hql, scpcId);
    return (List<SecurityConfigPublic>) list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.songzhi.app.dbParse.IMenuService#findSecurityConfigMenusByPublicId(java
   * .lang.String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SecurityConfigMenus> findSecurityConfigMenusByPublicId(String publicId) {
    String hql = "select t from SecurityConfigMenus t where t.securityConfigPublicId = ? and t.parentId is null order by t.serialNo asc";
    List<?> list = this.securityConfigMenusDAO.find(hql, publicId);
    return (List<SecurityConfigMenus>) list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.songzhi.app.dbParse.IMenuService#findSecurityConfigMenusByLevel(java.
   * lang.String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SecurityConfigMenus> findSecurityConfigMenusByLevel(String level) {
    String hql = "select t from SecurityConfigMenus t where t.menuLevel = ? order by t.serialNo asc";
    List<?> list = this.securityConfigMenusDAO.find(hql, level);
    return (List<SecurityConfigMenus>) list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.songzhi.app.dbParse.IMenuService#findSecurityConfigMenusByPid(java.lang
   * .String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<SecurityConfigMenus> findSecurityConfigMenusByPid(String parentId) {
    String hql = "select t from SecurityConfigMenus t where t.parentId = ? order by t.serialNo asc";
    List<?> list = this.securityConfigMenusDAO.find(hql, parentId);
    return (List<SecurityConfigMenus>) list;
  }

  public static Namespace ns = new Namespace("xs", "http://www.w3.org/2001/XMLSchema");

  /**
   * 给 sequence 节点添加模块类别
   * 
   * @param sequenceEle
   *          sequence 节点
   * @param menus
   *          菜单列表
   */
  public void addPublicClassSequence(Element sequenceEle, List<SecurityConfigPublicClass> publicClasses) {
    for (SecurityConfigPublicClass publicClass : publicClasses) {
      // 模块类别节点
      Element elePublicClass = sequenceEle.addElement(new QName("element", ns));
      elePublicClass.addAttribute("name", publicClass.getClassName());

      // 模块类别注解节点
      Element elePublicClassAnnotation = elePublicClass.addElement(new QName("annotation", ns));
      Element elePublicClassAnnotationDoc = elePublicClassAnnotation.addElement(new QName("documentation", ns));
      elePublicClassAnnotationDoc.setText("c1"/** publicClass.getCOMMENTS()==null?"":publicClass.getCOMMENTS() */
      );

      // 模块类别联合类型节点
      Element elePublicClassComplexType = elePublicClass.addElement(new QName("complexType", ns));
      Element complexTypeSequence = elePublicClassComplexType.addElement(new QName("sequence", ns));

      List<SecurityConfigPublicClass> publicClasses2 = findSecurityConfigPublicClassByParentId(publicClass.getId());
      if (publicClasses2 != null && publicClasses2.size() > 0) {
        addPublicClassSequence(complexTypeSequence, publicClasses2);
      } else {
        List<SecurityConfigPublic> publics = findSecurityConfigPublic(publicClass.getId());
        addPublicSequence(complexTypeSequence, publics);
      }

    }
  }

  /**
   * 给 sequence 节点添加菜单信息
   * 
   * @param sequenceEle
   *          sequence 节点
   * @param menus
   *          菜单列表
   */
  public void addPublicSequence(Element sequenceEle, List<SecurityConfigPublic> pubs) {
    for (SecurityConfigPublic pub : pubs) {
      // 模块类别节点
      Element elePublic = sequenceEle.addElement(new QName("element", ns));
      elePublic.addAttribute("name", pub.getReason());

      // 模块联合类型节点
      Element publicComplexType = elePublic.addElement(new QName("complexType", ns));
      Element publicComplexTypeSequence = publicComplexType.addElement(new QName("sequence", ns));

      List<SecurityConfigMenus> menus = findSecurityConfigMenusByPublicId(pub.getId());

      addMenuSequence(publicComplexTypeSequence, menus);

    }
  }

  /**
   * 给 sequence 节点添加菜单信息
   * 
   * @param sequenceEle
   *          sequence 节点
   * @param menus
   *          菜单列表
   */
  public void addMenuSequence(Element sequenceEle, List<SecurityConfigMenus> menus) {
    for (SecurityConfigMenus menu : menus) {
      // 菜单节点
      Element elePublic = sequenceEle.addElement(new QName("element", ns));
      elePublic.addAttribute("name", menu.getMenuDetail());

      List<SecurityConfigMenus> childs = findSecurityConfigMenusByPid(menu.getId());
      if (childs != null && childs.size() > 0) {
        // 菜单联合类型节点
        Element menuComplexType = elePublic.addElement(new QName("complexType", ns));
        Element menuComplexTypeSequence = menuComplexType.addElement(new QName("sequence", ns));
        addMenuSequence(menuComplexTypeSequence, childs);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.songzhi.app.dbParse.IMenuService#generate(java.lang.String)
   */
  @Override
  public void generate(String path, String tenantId) {
    Date startDate = new Date();
    Document doc = DocumentHelper.createDocument();
    Namespace ns = new Namespace("xs", "http://www.w3.org/2001/XMLSchema");
    QName root = new QName("schema", ns);
    Element rootEle = doc.addElement(root);
    QName elementFormDefault = new QName("elementFormDefault", null);
    rootEle.addAttribute(elementFormDefault, "qualified");
    QName attributeFormDefault = new QName("attributeFormDefault", null);
    rootEle.addAttribute(attributeFormDefault, "unqualified");

    Element ele1 = rootEle.addElement(new QName("element", ns));
    ele1.addAttribute("name", "区域平台");

    Element ele11 = ele1.addElement(new QName("annotation", ns));
    Element ele111 = ele11.addElement(new QName("documentation", ns));
    ele111.setText("Comment describing your root element");

    Element ele12 = ele1.addElement(new QName("complexType", ns));
    Element sequence = ele12.addElement(new QName("sequence", ns));

    List<SecurityConfigPublicClass> publicClasses = findSecurityConfigPublicClass(tenantId);
    addPublicClassSequence(sequence, publicClasses);

    // System.out.println(doc.asXML());

    // String xmlStr = doc.asXML();
    try {
      PrintWriter out = new PrintWriter(new File(path), "utf-8");
      out.print(doc.asXML());
      out.flush();
      out.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    Date endDate = new Date();
    System.out.println((endDate.getTime() - startDate.getTime()) / 1000 + "秒");
  }
}
