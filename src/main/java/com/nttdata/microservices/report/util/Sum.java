package com.nttdata.microservices.report.util;

import lombok.Data;

@Data
public class Sum {
  private final double value;
  private final int counter;

  public Sum(double value, int counter) {
    this.value = value;
    this.counter = counter;
  }

  public Sum add(double value) {
    return new Sum(this.value + value, this.counter + 1);
  }

  public double avg() {
    return value / counter;
  }

  public static Sum empty() {
    return new Sum(0, 0);
  }

}