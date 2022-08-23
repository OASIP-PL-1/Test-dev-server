package com.example.backend.validator;

import com.example.backend.entities.Role;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RoleValidator implements ConstraintValidator<RoleConstraint, String> {

    Role[] roles = Role.values();
//    String[] roleString = (String[]) Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList()).toArray();


    @Override
    public void initialize(RoleConstraint constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String role, ConstraintValidatorContext constraintValidatorContext) {
        for(Role r : roles){
            System.out.println(r);
            System.out.println(r.toString());
            if(r.toString().equals(role)) return true;
        }
//        System.out.println(roles[1].toString().equals(role));
        return false;
    }
}
