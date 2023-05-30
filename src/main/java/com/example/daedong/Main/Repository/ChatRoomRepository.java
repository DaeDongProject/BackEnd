package com.example.daedong.Main.Repository;

import com.example.daedong.Dto.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
