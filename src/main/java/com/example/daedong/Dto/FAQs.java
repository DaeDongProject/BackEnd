package com.example.daedong.Dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "FAQs")
public class FAQs {
    private String id;
    private String topic;
    private String subTopic;
    private List<Object> FAQ;
}
