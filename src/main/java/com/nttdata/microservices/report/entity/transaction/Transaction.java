package com.nttdata.microservices.report.entity.transaction;

import com.nttdata.microservices.report.entity.account.Account;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

  private String id;
  private String transactionCode;
  private TransactionType transactionType;
  private Double amount;
  private Account account;
  private LocalDateTime registerDate;

}
