package com.anq.library_management_system.validation;

import com.anq.library_management_system.validation.CurrentOrPastYearValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CurrentOrPastYearValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentOrPastYear {

    String message() default "Publication year cannot be in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}