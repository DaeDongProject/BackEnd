package com.example.daedong.Dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ChatRoom")
public class ChatRoom {
    private String _id;
    private String userId;
    private String chatTitle;
    private Object[] contextUser;
    private boolean deleteYn;
}
