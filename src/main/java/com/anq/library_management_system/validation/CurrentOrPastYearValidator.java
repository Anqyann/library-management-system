package com.anq.library_management_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CurrentOrPastYearValidator
        implements ConstraintValidator<CurrentOrPastYear, Integer> {

    @Override
    public boolean isValid(Integer value,
                           ConstraintValidatorContext context) {

        int currentYear = Year.now().getValue();

        return value <= currentYear;
    }
}