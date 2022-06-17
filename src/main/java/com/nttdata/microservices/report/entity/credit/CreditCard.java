package com.nttdata.microservices.report.entity.credit;

import com.nttdata.microservices.report.entity.client.Client;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreditCard {

  private String id;
  private String cardNumber;
  private Client client;
  private LocalDate expirationDate;

}
