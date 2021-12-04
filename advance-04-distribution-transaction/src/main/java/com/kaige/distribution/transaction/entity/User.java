package com.kaige.distribution.transaction.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

  private String userName;

  private Byte userAge;
}
