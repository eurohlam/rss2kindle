package org.roag.web;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.roag.pages.SignUpPage;

import static org.roag.pages.PageUtils.to;

@DisplayName("SignUp Page Tests")
public class SignUpTest {

    @Test
    @Disabled
    void passwordValidationTest() {
        to(SignUpPage.class)
                .signUpWith("test", "test", "test@test.com");
    }
}
