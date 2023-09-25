package com.example.daedong.Menu.Controller;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.PastChatRoom;
import com.example.daedong.Dto.User;
import com.example.daedong.Menu.Service.MenuServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/daedong/menu")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MenuController {

    private final MenuServiceImpl menuService;

    @PostMapping("/createchatroom")
    public String CreateChatRoom(@RequestBody User user){
        String ObjectId = menuService.createChatRoom(user);
        menuService.insertChatRoomOnjectId(user,ObjectId);
        return ObjectId;
    }

    @GetMapping("/pastList/{userId}")
    public List<PastChatRoom> PastList(@PathVariable String userId) throws Exception{
        return menuService.selectPastChatTitle(userId);
    }

    @PostMapping("/updatetitle/{newChatTitle}")
    public String UpdateChatTitle(@RequestBody ChatRoom chatRoom, @PathVariable String newChatTitle){
        return menuService.updateChatTitle(chatRoom.get_id(), newChatTitle);
    }

    @PostMapping("deletechatroom")
    public String DeleteChatRoom(@RequestBody ChatRoom chatRoom){
        return menuService.deleteChatRoom(chatRoom.get_id());
    }
}
