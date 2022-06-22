package com.nttdata.microservices.report.service.dto;

import com.nttdata.microservices.report.entity.client.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

  private String accountNumber;
  private String cci;
  private Double amount;
  private String productType;
  private Client client;

}
