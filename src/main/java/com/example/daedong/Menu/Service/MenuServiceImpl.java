package com.example.daedong.Menu.Service;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.PastChatRoom;
import com.example.daedong.Dto.User;
import com.example.daedong.Main.Repository.ChatRoomRepository;
import com.example.daedong.Main.Repository.UserRepository;
import com.google.api.gax.rpc.ApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MenuServiceImpl implements MenuService{

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final ChatRoomRepository chatRoomRepository;

    Update update = new Update();
    List<Document> array = new ArrayList<>();
    org.bson.Document item = new org.bson.Document();



    public MenuServiceImpl(UserRepository userRepository, MongoTemplate mongoTemplate, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.chatRoomRepository = chatRoomRepository;
    }


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

    @Override
    public List<PastChatRoom> selectPastChatTitle(String userId) {
        List<String> chatRoomObjectId = new ArrayList<>();
        List<String> chatTitle = new ArrayList<>();
        List<PastChatRoom> pastChatRooms = new ArrayList<>();
        chatRoomObjectId = userRepository.findById(userId).get().getChatRoomOid();
        for(int i = 0; i<chatRoomObjectId.size(); i++){
            PastChatRoom pastChatRoom = new PastChatRoom();
            pastChatRoom.setObjectId(chatRoomObjectId.get(i));
            pastChatRoom.setChatTitle(chatRoomRepository.findById(chatRoomObjectId.get(i)).get().getChatTitle());
            pastChatRooms.add(pastChatRoom);
        }

        return pastChatRooms;
    }

}
