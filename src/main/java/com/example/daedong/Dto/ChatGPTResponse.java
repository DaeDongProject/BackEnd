package com.example.daedong.Dto;

import com.example.daedong.Dto.ChatGptChoice;
import lombok.Data;

import java.util.List;

@Data
public class ChatGPTResponse {
    private List<ChatGptChoice> choices;
}
