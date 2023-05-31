package com.example.daedong.Menu.Service;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.User;
import com.example.daedong.Main.Repository.ChatRoomRepository;
import com.example.daedong.Main.Repository.UserRepository;
import com.google.api.gax.rpc.ApiException;
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
import java.util.List;

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
        Object[] objects = {};
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

}
