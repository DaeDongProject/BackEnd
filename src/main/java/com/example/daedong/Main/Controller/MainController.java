package com.example.daedong.Main.Controller;

import com.example.daedong.Dto.AnswerRequestDto;
import com.example.daedong.Main.Repository.ChatRepository;
import com.example.daedong.Main.Service.MainServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/daedong")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MainController {
    private static final String PROJECT_ID = "firstagent-mglo";
    private static final String SESSION_ID = UUID.randomUUID().toString();

    private final ChatRepository chatRepository;
    private final MainServiceImpl mainService;

    public MainController(ChatRepository chatRepository, MainServiceImpl mainService) {
        this.chatRepository = chatRepository;
        this.mainService = mainService;
    }

    @PostMapping("/chatroom/question")
    public String AnswerRequest(@RequestBody AnswerRequestDto answerRequestDto) throws IOException {
        String answer = mainService.detectIntentTexts(answerRequestDto.getId(), PROJECT_ID, answerRequestDto.getQuestion(), SESSION_ID, "ko-KR");

        if(answer.equals("fallback"))
            return mainService.processSearch(answerRequestDto.getId(), answerRequestDto.getQuestion());
        else
            return answer;
    }
}
