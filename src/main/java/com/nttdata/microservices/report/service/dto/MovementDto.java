package com.nttdata.microservices.report.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovementDto {

  private Double amount;
  private String transactionCode;
  private MovementType movementType;
  private LocalDateTime registerDate;

}
