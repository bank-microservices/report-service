package com.nttdata.microservices.report.entity.credit;

import com.nttdata.microservices.report.entity.client.Client;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit implements Serializable {

  private String id;
  private String accountNumber;
  private Client client;
  private Double amount;
  private Double creditLimit;
  private CreditCard creditCard;

}
