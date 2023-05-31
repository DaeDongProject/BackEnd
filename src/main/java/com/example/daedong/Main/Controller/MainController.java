package com.example.daedong.Main.Controller;

import com.example.daedong.Dto.AnswerRequestDto;
import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.ModifyRequestDto;
import com.example.daedong.Main.Repository.UserRepository;
import com.example.daedong.Main.Service.MainServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/daedong")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MainController {
    private static final String PROJECT_ID = "daedong-nnlp";
    private static final String SESSION_ID = UUID.randomUUID().toString();

    private final UserRepository userRepository;
    private final MainServiceImpl mainService;

    public MainController(UserRepository chatRepository, UserRepository userRepository, MainServiceImpl mainService) {
        this.userRepository = userRepository;
        this.mainService = mainService;
    }

    @PostMapping("/chatroom/question")
    public String AnswerRequest(@RequestBody AnswerRequestDto answerRequestDto) throws IOException {
        String answer = mainService.detectIntentTexts(answerRequestDto.getId(), PROJECT_ID, answerRequestDto.getQuestion(), SESSION_ID, "ko-KR");

        if (answer.equals("fallback"))
            return mainService.processSearch(answerRequestDto.getId(), answerRequestDto.getQuestion());
        else
            return answer;
    }

    @GetMapping("/{userId}")
    public ChatRoom NewestChat(@PathVariable String userId) {
        String chatId = mainService.findChatRoomObjectId(userId);
        return mainService.findById(chatId);
    }

    @GetMapping("/chatroom/{chatRoomId}")
    public ChatRoom SelectedChat(@PathVariable String chatRoomId) {
        return mainService.findById(chatRoomId);
    }

    @PostMapping("/chatroom/modify")
    public String ModifyRequest(@RequestBody ModifyRequestDto modifyRequestDto){
        return mainService.modifyInformation(modifyRequestDto);
    }
}
