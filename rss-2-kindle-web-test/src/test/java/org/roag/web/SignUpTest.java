package org.roag.web;

import org.junit.jupiter.api.Test;
import org.roag.pages.SignUpPage;

import static org.roag.pages.PageUtils.to;

public class SignUpTest {

    @Test
    void passwordValidationTest() {
        to(SignUpPage.class)
                .signUpWith("test", "test", "test@test.com");
    }
}
