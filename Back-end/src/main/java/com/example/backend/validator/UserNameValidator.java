package com.example.backend.validator;

import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNameValidator implements ConstraintValidator<UserNameConstraint,String> {
    @Autowired
    UserRepository repository;

    @Override
    public void initialize(UserNameConstraint constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext cxt) {
//        repository.findByUserName(userName);
//        System.out.println(repository.findByUserName(userName).toString());
        return repository.findByUserName(userName.trim())==null;
    }
}
