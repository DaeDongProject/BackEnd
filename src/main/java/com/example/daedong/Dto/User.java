package com.example.daedong.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "User")
public class User {
    @Id
    private String _id;
    private String name;
    private String phoneNumber;
    private String schoolEmail;
    private String password;
    private String schoolName;
    private boolean pushAlarm;
    private boolean personalInformation;
    private List<String> chatRoomOid;

    @Builder
    public User(String name, String phoneNumber, String schoolEmail, String password, String schoolName, boolean pushAlarm, boolean personalInformation) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.schoolEmail = schoolEmail;
        this.password = password;
        this.schoolName = schoolName;
        this.pushAlarm = pushAlarm;
        this.personalInformation = personalInformation;
    }

    // 로그인 후 서비스 이용할 때 User 객체 password ""로 보내기
//    public void clearPassword() {
//        this.password = "";
//    }
}
