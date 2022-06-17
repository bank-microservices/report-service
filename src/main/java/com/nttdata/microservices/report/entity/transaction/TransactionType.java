package com.nttdata.microservices.report.entity.transaction;

public enum TransactionType {
  DEPOSIT, WITHDRAWAL;

  public boolean isDeposit() {
    return this.equals(DEPOSIT);
  }
}
