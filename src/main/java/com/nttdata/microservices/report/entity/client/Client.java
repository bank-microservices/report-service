package com.nttdata.microservices.report.entity.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Client {
  private String id;
  private String documentNumber;
  private String firstNameBusiness;
  private String surnames;
}
