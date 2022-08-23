package com.example.backend.validator;

import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserEmailValidator implements ConstraintValidator<UserEmailConstraint,String> {

    @Autowired
    UserRepository repository;

    @Override
    public void initialize(UserEmailConstraint constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String userEmail, ConstraintValidatorContext constraintValidatorContext) {
        return repository.findByUserEmail(userEmail.trim()) == null;
    }
}
