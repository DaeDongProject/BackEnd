package com.example.daedong.Menu.Controller;

import com.example.daedong.Dto.PastChatRoom;
import com.example.daedong.Dto.User;
import com.example.daedong.Menu.Service.MenuServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/daedong/menu")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MenuController {

    private final MenuServiceImpl menuService;

    public MenuController(MenuServiceImpl menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/createchatroom")
    public String CreateChatRoom(@RequestBody User user){
        String ObjectId = menuService.createChatRoom(user);
        menuService.insertChatRoomOnjectId(user,ObjectId);
        log.info("ChatRoom ObjectId : ", ObjectId);
        log.info("User id : ", user.get_id());
        return ObjectId;
    }

    @GetMapping("/pastList/{userId}")
    public List<PastChatRoom> PastList(@PathVariable String userId) throws Exception{
        return menuService.selectPastChatTitle(userId);
    }
}
