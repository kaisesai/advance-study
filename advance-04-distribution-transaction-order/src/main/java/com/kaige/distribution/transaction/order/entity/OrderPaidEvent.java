package com.kaige.distribution.transaction.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderPaidEvent implements Serializable {

  private String orderId;

  private BigDecimal paidMoney;
}
