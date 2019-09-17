package org.roag.web;

import org.junit.jupiter.api.*;
import org.roag.pages.SignUpPage;

import static org.roag.pages.PageUtils.to;

@DisplayName("SignUp Page Tests")
@Tag("SECURITY")
public class SignUpTest {


    @Test
    @DisplayName("Test password confirmation")
    void passwordConfirmationTest() {
        String error = to(SignUpPage.class)
                .setUsername("robot")
                .setEmail("robot@email.com")
                .setPassword("robot1")
                .setConfirmPassword("robot2")
                .clickSignUp()
                .errorMessage();
        Assertions.assertTrue(error != null && error.contains("confirmation of password does not match"));
    }

    @Test
    @DisplayName("Test if username already exists")
    void existingUserTest() {
        to(SignUpPage.class)
                .signUpWith("robot", "robot1", "robot@mail.com");
        String error = to(SignUpPage.class)
                .signUpWith("robot", "robot1", "robot@mail.com")
                .errorMessage();

        Assertions.assertTrue(error != null && error.contains("username is already occupied"));
    }
}
