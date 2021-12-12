package com.kaige.distribution.transaction.order.entity;

import lombok.Data;

@Data
public class ProductWithPayload<T> {

  private String productName;

  private T payload;
}
