package com.nttdata.microservices.report.entity.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Client {
  private String id;
  private String documentNumber;
  private String firstNameBusiness;
  private String surnames;
}
