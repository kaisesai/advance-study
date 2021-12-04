package com.kaige.distribution.transaction.entity;

import lombok.Data;

@Data
public class ProductWithPayload<T> {

  private String productName;

  private T payload;
}
