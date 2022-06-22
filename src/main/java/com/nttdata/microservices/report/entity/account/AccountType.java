package com.nttdata.microservices.report.entity.account;

// 1: saving | 2: current | 3: fixed term

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountType {
  private String id;
  private String code;
  private String description;

  public AccountType(String id, String code) {
    this.id = id;
    this.code = code;
  }

}
