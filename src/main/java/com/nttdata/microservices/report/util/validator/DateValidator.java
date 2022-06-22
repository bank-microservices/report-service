package com.nttdata.microservices.report.util.validator;

import java.text.SimpleDateFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

  private String format;

  @Override
  public void initialize(ValidDate constraintAnnotation) {
    this.format = constraintAnnotation.format();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (StringUtils.isBlank(value)) {
      return false;
    }
    if (value.length() != format.length()) {
      return false;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      simpleDateFormat.parse(value);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
