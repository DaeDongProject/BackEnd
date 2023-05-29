package com.example.daedong.Dto;

import lombok.Data;

@Data
public class Context {
    private String question;
    private String answer;
    private boolean isDialogflow;
    private boolean modifyRequest;
    private String modifyText;
}
