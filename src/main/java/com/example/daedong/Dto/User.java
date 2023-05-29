package com.example.daedong.Dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
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
    private String[] chatRoomOid;
}
