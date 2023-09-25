package com.example.daedong.Dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class UserForm {
    private String name;
    private String phoneNumber;
    private String schoolEmail;
    private String password;
    private String schoolName;
    private boolean pushAlarm;
    private boolean personalInformation;

    public void encodingPassword(PasswordEncoder passwordEncoder){
        if(StringUtils.isEmpty(password)){
            return;
        }
        password = passwordEncoder.encode(password);
    }
}
