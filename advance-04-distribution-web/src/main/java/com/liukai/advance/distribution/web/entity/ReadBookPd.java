package com.liukai.advance.distribution.web.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * (ReadBookPd)实体类
 *
 * @author liukai
 * @since 2021-01-02 19:43:59
 */
@Data
@Document(indexName = "book")
public class ReadBookPd implements Serializable {
  
  private static final long serialVersionUID = -89138532903416045L;
  
  @Id
  private Integer id;
  
  @Field(type = FieldType.Text)
  private String name;
  
  @Field(type = FieldType.Text)
  private String enName;
  
  @Field(type = FieldType.Keyword)
  private String author;
  
  @Field(type = FieldType.Text)
  private String imgurl;
  
  @Field(type = FieldType.Text)
  private String description;
  
  @Field(type = FieldType.Long)
  private Date createTime;
  
  @Field(type = FieldType.Integer)
  private Integer creator;
  
  @Field(type = FieldType.Long)
  private Date updateTime;
  
  /**
   * 1正常，-1删除，0下架
   */
  @Field(type = FieldType.Integer)
  private Integer status;
  
  /**
   * 评论数
   */
  @Field(type = FieldType.Integer)
  private Integer commentNum;
  
  /**
   * 价格，分
   */
  @Field(type = FieldType.Integer)
  private Integer price;
  
  @Field(type = FieldType.Text)
  private String category;
  
}
