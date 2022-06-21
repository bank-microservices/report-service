package com.nttdata.microservices.report.entity.credit;

import com.nttdata.microservices.report.entity.client.Client;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreditProduct {

  private String id;
  private String accountNumber;
  private Client client;
  private CreditProductType creditProductType;
  private String cci;
  private Double creditLimit;
  private Double amount;
  private String cardNumber;
  private String cvv;
  private boolean status;
  private LocalDate expirationDate;

}
