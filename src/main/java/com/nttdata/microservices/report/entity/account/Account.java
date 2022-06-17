package com.nttdata.microservices.report.entity.account;

import com.nttdata.microservices.report.entity.client.Client;
import lombok.Data;

@Data
public class Account {

  private String id;
  private String accountNumber;
  private String cci;
  private Double amount;
  private Double maintenanceFee;
  private Integer maxLimitMonthlyMovements;
  private AccountType accountType;
  private Client client;
}
