package com.example.daedong.Main.Service;

import com.example.daedong.Dto.ChatRoom;
import com.example.daedong.Dto.ModifyRequestDto;
import com.google.api.gax.rpc.ApiException;
import java.io.IOException;
import java.util.ArrayList;

public interface MainService {
    String detectIntentTexts(String chatId, String projectId, String question, String sessionId, String languageCode) throws IOException, ApiException;

    ArrayList<String> encodingTexts(String encodedText);
    String convertToUnicodeOctal(char character);
    String convertToOctalString(int value);

    String processSearch(String chatId, String question);

    String findChatRoomObjectId(String id);

    ChatRoom findById(String id);

    String modifyInformation(ModifyRequestDto modifyRequestDto);
}
