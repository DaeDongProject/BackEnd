package com.example.daedong.Main.Service;

import com.example.daedong.Dto.ChatRoom;
import com.google.api.gax.rpc.ApiException;
import java.io.IOException;

public interface MainService {
    String detectIntentTexts(String chatId, String projectId, String question, String sessionId, String languageCode) throws IOException, ApiException;

    String processSearch(String chatId, String question);

    String findChatRoomObjectId(String id);

    ChatRoom findById(String id);
}
