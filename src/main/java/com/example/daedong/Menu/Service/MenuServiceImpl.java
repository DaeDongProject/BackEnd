package com.example.daedong.Menu.Service;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.PastChatRoom;
import com.example.daedong.Dto.User;
import com.example.daedong.Dto.UserForm;
import com.example.daedong.Main.Repository.ChatRoomRepository;
import com.example.daedong.Main.Repository.UserRepository;
import com.google.api.gax.rpc.ApiException;
import com.mongodb.client.result.UpdateResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService{

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final PasswordEncoder passwordEncoder;
    Update update = new Update();
    List<Document> array = new ArrayList<>();
    org.bson.Document item = new org.bson.Document();

    @Override
    public String createChatRoom(User user) {
        ChatRoom chatRoom = new ChatRoom();
        List<Object> objects = new ArrayList<>();
        chatRoom.setUserId(user.get_id());
        chatRoom.setChatTitle("New Chat");
        chatRoom.setDeleteYn(false);
        chatRoom.setContextUser(objects);

        chatRoomRepository.save(chatRoom);
        return(chatRoom.get_id());
    }

    @Override
    public String insertChatRoomOnjectId(User user, String objectId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(new ObjectId(user.get_id())));
        update.push("chatRoomOid",objectId);
        mongoTemplate.updateFirst(query, update,"User");
        return "true";
    }

    // chatRoom deleteYn이 false일 때로 변경
    @Override
    public List<PastChatRoom> selectPastChatTitle(String userId) {
        List<String> chatRoomObjectId = new ArrayList<>();
        ChatRoom chatRoom = new ChatRoom();
        List<PastChatRoom> pastChatRooms = new ArrayList<>();
        chatRoomObjectId = userRepository.findById(userId).get().getChatRoomOid();



        for(int i = 0; i<chatRoomObjectId.size(); i++){
            if(chatRoomRepository.findById(chatRoomObjectId.get(i)).get().isDeleteYn() == false) {
                PastChatRoom pastChatRoom = new PastChatRoom();
                pastChatRoom.setObjectId(chatRoomObjectId.get(i));
                pastChatRoom.setChatTitle(chatRoomRepository.findById(chatRoomObjectId.get(i)).get().getChatTitle());
                pastChatRooms.add(pastChatRoom);
            } else continue;
        }

        return pastChatRooms;
    }

    @Override
    public String updateChatTitle(String objectId, String newChatTitle) {
        ChatRoom chatRoom = chatRoomRepository.findById(objectId).orElse(null);    //    if(chatRoom.isDeleteYn() == false){
        chatRoom.setChatTitle(newChatTitle);
        chatRoomRepository.save(chatRoom);

        return "success";
    }

    @Override
    public String deleteChatRoom(String ObjectId) {
        ChatRoom chatRoom = chatRoomRepository.findById(ObjectId).orElse(null);
        chatRoom.setDeleteYn(true);
        chatRoomRepository.save(chatRoom);
        return "success";
    }

    @Override
    @Transactional
    public boolean deleteBySchoolEmail(User user) {
        try {
            userRepository.deleteById(user.get_id());
            System.out.println("회원 삭제 완료");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean update(User user) {
        try {
            userRepository.save(user);
            System.out.println("회원 정보 수정 완료");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
