package com.nttdata.microservices.report.entity.transaction;

import com.nttdata.microservices.report.entity.credit.Credit;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Payment {

  private String id;
  private String transactionCode;
  private Double amount;
  private Credit credit;
  private LocalDateTime registerDate;
}
