package org.roag.web;

import org.roag.security.SecurityService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by eurohlam on 6/03/18.
 */
public class NewUserFormValidator implements Validator {
    private SecurityService securityService;

    public NewUserFormValidator(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean supports(Class<?> inputClass) {
        return NewUserForm.class.isAssignableFrom(inputClass);
    }

    @Override
    public void validate(Object newUserForm, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "required.confirmPassword");

        if (newUserForm != null) {
            NewUserForm user = (NewUserForm) newUserForm;
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "match.confirmPassword");
            } else if (user.getUsername() != null && user.getUsername().trim().length() != 0
                    && securityService.isUserExist(user.getUsername())) {
                errors.rejectValue("username", "match.username");
            }
        }
    }
}
