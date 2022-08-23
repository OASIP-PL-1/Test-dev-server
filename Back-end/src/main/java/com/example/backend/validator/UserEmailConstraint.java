package com.example.backend.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UserEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserEmailConstraint {
    String message() default "This email is already existed.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
