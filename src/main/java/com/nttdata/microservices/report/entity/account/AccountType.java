package com.nttdata.microservices.report.entity.account;

// 1: saving | 2: current | 3: fixed term

import lombok.Data;

@Data
public class AccountType {
  private String id;
  private String code;
  private String description;

}
