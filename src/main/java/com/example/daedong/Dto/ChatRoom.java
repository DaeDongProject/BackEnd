package com.example.daedong.Dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "ChatRoom")
public class ChatRoom {
    private String _id;
    private String userId;
    private String chatTitle;
    private List<Object> contextUser;
    private boolean deleteYn;
}
