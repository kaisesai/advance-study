package com.kaige.advance.reflect.orm.step4;

import com.kaige.advance.reflect.orm.Column;
import com.kaige.advance.reflect.orm.entity.UserEntity2;
import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类工厂
 */
@Slf4j
public class EntityHelperFactory {
  
  /**
   * 实体类辅助器 map
   */
  private static final Map<Class<?>, AbstractEntityHelper> CLASS_ABSTRACT_ENTITY_HELPER_MAP
    = new ConcurrentHashMap<>();
  
  public static void main(String[] args) {
    AbstractEntityHelper entityHelper = getEntityHelper(UserEntity2.class);
    System.out.println("entityHelper = " + entityHelper);
    
    entityHelper.createEntity(null);
  }
  
  public static <T> AbstractEntityHelper<T> getEntityHelper(Class<T> entityClazz) {
    if (Objects.isNull(entityClazz)) {
      return null;
    }
    
    AbstractEntityHelper entityHelper = CLASS_ABSTRACT_ENTITY_HELPER_MAP.get(entityClazz);
    if (Objects.nonNull(entityHelper)) {
      return entityHelper;
    }
    
    // 开始构建
    synchronized (entityClazz) {
      // javassist 字节码工具包动态生成字节码
      // 获取类池信息
      ClassPool classPool = ClassPool.getDefault();
      
      // 导入相关的类，生成如下代码
      // import java.sql.ResultSet
      // import com.kaige.advance.reflect.orm.entity.UserEntity2
      // import ...
      classPool.importPackage(ResultSet.class.getName());
      classPool.importPackage(entityClazz.getName());
      
      try {
        // 抽象辅助器
        CtClass abstractEntityHelperClazz = classPool
          .getCtClass(AbstractEntityHelper.class.getName());
        // 辅助器实现类的名称
        String helperImplClazzName = entityClazz.getName() + "_Helper";
        
        // 创建辅助器
        // public class UserEntity_Helper extends AbstractEntityHelper { ...
        CtClass helperClazz = classPool.makeClass(helperImplClazzName, abstractEntityHelperClazz);
        
        // 创建默认构造器
        // public UserEntity_Helper() {}
        CtConstructor constructor = new CtConstructor(new CtClass[0], helperClazz);
        // 空函数体
        constructor.setBody("{}");
        // 添加默认构造器
        helperClazz.addConstructor(constructor);
        
        // 用于创建函数代码字符串
        StringBuilder sb = new StringBuilder();
        // 添加一个方法，即实现抽象类中的 createEntity 方法
        sb.append("public Object createEntity(java.sql.ResultSet resultSet) throws Exception {\n");
        // 生成以下代码
        // UserEntity obj = new UserEntity();
        sb.append(entityClazz.getName()).append(" obj = new ").append(entityClazz.getName())
          .append("();\n");
        
        // 通过反射方式获取类的字段数组，并生成代码
        
        // 获取类的字段数组，并生成代码
        Field[] fields = entityClazz.getDeclaredFields();
        for (Field field : fields) {
          // 获取自定义注解 Column
          Column column = field.getAnnotation(Column.class);
          if (Objects.isNull(column)) {
            continue;
          }
          // 获取列名称
          String colName = column.value();
          
          int l2 = field.getName().length() - 1;
          String m = "";
          if (l2 > 0) {
            m = field.getName().substring(1);
          }
          
          String setMethodName = "set" + StringUtils
            .upperCase(field.getName().subSequence(0, 1).toString()) + m;
          System.out.println("setMethodName = " + setMethodName);
          // 获取字段类型
          if (field.getType() == Integer.class) {
            // 生成如下代码
            // obj.setAge(resultSet.getInt("age"));
            // 注意 int 类型是不能直接写入到参数类型为 Integer 的参数值上去的，会报错
            sb.append("obj.").append(setMethodName).append("(")
              .append("new Integer(resultSet.getInt(\"").append(colName).append("\"))")
              .append(");\n");
          } else if (field.getType() == Long.class) {
            // 生成如下代码
            // obj.setId(resultSet.getLong("id"));
            sb.append("obj.").append(setMethodName).append("(")
              .append("new Long(resultSet.getLong(\"").append(colName).append("\"))")
              .append(");\n");
          } else if (field.getType() == String.class) {
            // 生成如下代码
            // obj.setName(resultSet.getString("name"));
            sb.append("obj.").append(setMethodName).append("(").append("resultSet.getString(\"")
              .append(colName).append("\")").append(");\n");
          } else if (field.getType() == Date.class) {
            // 生成如下代码
            // obj.setCreateTime(resultSet.getTimestamp("create_time"));
            // obj.setUpdateTime(resultSet.getTimestamp("update_time"));
            sb.append("obj.").append(setMethodName).append("(").append("resultSet.getTimestamp(\"")
              .append(colName).append("\")").append(");\n");
          } else {
            // 接着写其他的类型即可
          }
          
        }
        
        sb.append("return obj;\n");
        sb.append("}");
        
        // 创建解析方法
        CtMethod ctMethod = CtNewMethod.make(sb.toString(), helperClazz);
        // 添加方法
        helperClazz.addMethod(ctMethod);
        // 调试文件
        // helperClazz.writeFile("/Users/liukai/IdeaProjects/liukai/advance-study/advance-03-jvm/src/main/resources/");
        // 获取 Java 类
        Class<?> javaClazz = helperClazz.toClass();
        
        Method[] declaredMethods = javaClazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
          System.out.println("declaredMethod = " + declaredMethod);
        }
        
        AbstractEntityHelper abstractEntityHelper = (AbstractEntityHelper) javaClazz.newInstance();
        
        CLASS_ABSTRACT_ENTITY_HELPER_MAP.put(entityClazz, abstractEntityHelper);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    return CLASS_ABSTRACT_ENTITY_HELPER_MAP.get(entityClazz);
    
  }
  
}
