package com.example.daedong.Menu.Service;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.PastChatRoom;
import com.example.daedong.Dto.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface MenuService {


    String createChatRoom(User user);

    String insertChatRoomOnjectId(User user, String ObjectId);

    List<PastChatRoom> selectPastChatTitle(String userId);

    String updateChatTitle(String ObjectId, String newChatTitle);

    String deleteChatRoom(String ObjectId);
}
