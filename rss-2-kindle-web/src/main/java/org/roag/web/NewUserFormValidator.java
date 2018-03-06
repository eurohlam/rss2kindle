package org.roag.web;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by eurohlam on 6/03/18.
 */
public class NewUserFormValidator implements Validator
{
    @Override
    public boolean supports(Class<?> aClass)
    {
        return NewUserForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "required.confirmPassword");
    }
}
