package com.nttdata.microservices.report.util.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface ValidDate {

  String message() default "wrong date format";

  String format() default "yyyy-MM-dd";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};


}
