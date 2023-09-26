package com.example.daedong.Menu.Service;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.PastChatRoom;
import com.example.daedong.Dto.User;
import com.example.daedong.Dto.UserForm;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MenuService {

    String createChatRoom(User user);

    String insertChatRoomOnjectId(User user, String ObjectId);

    List<PastChatRoom> selectPastChatTitle(String userId);

    String updateChatTitle(String ObjectId, String newChatTitle);

    String deleteChatRoom(String ObjectId);

    // 회원 정보 수정
    boolean update(User user);

    // 회원 정보 삭제
    boolean deleteBySchoolEmail(User user);
}
