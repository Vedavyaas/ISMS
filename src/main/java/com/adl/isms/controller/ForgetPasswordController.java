package com.adl.isms.controller;

import com.adl.isms.service.ForgetPasswordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForgetPasswordController {

    private final ForgetPasswordService forgetPasswordService;

    public ForgetPasswordController(ForgetPasswordService forgetPasswordService) {
        this.forgetPasswordService = forgetPasswordService;
    }

    @PostMapping("/password/reset")
    public String passwordReset(String username, String newPassword) {
        //OTP will be enabled at production time, Kept simple for testing purposes.
        return forgetPasswordService.passwordReset(username, newPassword);
    }
}
