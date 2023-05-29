package com.example.daedong.Main.Service;

import com.google.api.gax.rpc.ApiException;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

public interface MainService {
    String detectIntentTexts(String chatId, String projectId, String question, String sessionId, String languageCode) throws IOException, ApiException;

    String processSearch(String chatId, String question);
}
