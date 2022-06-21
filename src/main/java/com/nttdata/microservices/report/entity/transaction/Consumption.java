package com.nttdata.microservices.report.entity.transaction;

import com.nttdata.microservices.report.entity.credit.CreditProduct;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Consumption {

  private String id;
  private String transactionCode;
  private Double amount;
  private CreditProduct creditCard;
  private LocalDateTime registerDate;

}
