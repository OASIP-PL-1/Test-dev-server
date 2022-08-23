package com.example.backend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleConstraint {
    String message() default "Role must be either 'student', 'lecturer' or 'admin'.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
