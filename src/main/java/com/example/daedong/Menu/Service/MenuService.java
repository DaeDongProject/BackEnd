package com.example.daedong.Menu.Service;

import com.example.daedong.Dto.User;
import org.springframework.stereotype.Service;

public interface MenuService {


    String createChatRoom(User user);

    String insertChatRoomOnjectId(User user, String ObjectId);

}
