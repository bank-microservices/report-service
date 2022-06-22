package com.nttdata.microservices.report.entity.account;

import com.nttdata.microservices.report.entity.client.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

  private String id;
  private String accountNumber;
  private String cci;
  private Double amount;
  private Double maintenanceFee;
  private Double transactionFee;
  private Integer maxLimitMonthlyMovements;
  private AccountType accountType;
  private Client client;

}
