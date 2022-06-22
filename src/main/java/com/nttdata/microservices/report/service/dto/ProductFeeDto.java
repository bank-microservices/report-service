package com.nttdata.microservices.report.service.dto;

import com.nttdata.microservices.report.entity.account.AccountType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFeeDto {

  private AccountType accountType;
  private Double totalFee;

}
