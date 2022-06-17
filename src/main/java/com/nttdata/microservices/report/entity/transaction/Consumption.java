package com.nttdata.microservices.report.entity.transaction;

import com.nttdata.microservices.report.entity.credit.Credit;
import com.nttdata.microservices.report.entity.credit.CreditCard;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Consumption {

  private String id;
  private String transactionCode;
  private Double amount;
  private CreditCard creditCard;
  private Credit credit;
  private LocalDateTime registerDate;

}
