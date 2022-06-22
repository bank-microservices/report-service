package com.nttdata.microservices.report.entity.transaction;

import com.nttdata.microservices.report.entity.account.Account;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Transaction {

  private String id;
  private String transactionCode;
  private Double amount;
  private Double transactionFee;
  private Account account;
  private LocalDateTime registerDate;
  private TransactionType transactionType;

}
