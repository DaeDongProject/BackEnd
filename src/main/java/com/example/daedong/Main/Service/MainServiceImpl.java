package com.example.daedong.Main.Service;

import com.example.daedong.Dto.*;
import com.example.daedong.Dto.Context;
import com.example.daedong.Main.Repository.ChatRoomRepository;
import com.example.daedong.Main.Repository.UserRepository;
import com.google.api.gax.rpc.ApiException;
//import com.google.cloud.dialogflow.v2.*;
import com.google.cloud.dialogflow.v2.*;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MainServiceImpl implements MainService {

    @Value("${OPEN_AI_URL}")
    private String OPEN_AI_URL;

    @Value("${OPEN_AI_KEY}")
    private String OPEN_AI_KEY;

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MainServiceImpl(MongoTemplate mongoTemplate, UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    Update update = new Update();
    List<Document> array = new ArrayList<>();
    org.bson.Document item = new org.bson.Document();

    // get answer from DialogFlow
    @Override
    public String detectIntentTexts(String chatId, String projectId, String question, String sessionId, String languageCode) throws IOException, ApiException {
        Query query = new Query().addCriteria(Criteria.where("_id").is(new ObjectId(chatId)));

        QueryResult queryResult;
        String decodedText;

        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            //Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path : " + session.toString());

            // Detect intents text input
            // Set the text (hello) and language code (en-US) for the query
            TextInput.Builder textInput =
                    TextInput.newBuilder().setText(question).setLanguageCode(languageCode);

            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            // Display the query result
            queryResult = response.getQueryResult();

            // Convert fulfillmentMessages to String
            String encodedText = String.valueOf(queryResult.getFulfillmentMessages(0).getText());
            encodedText = encodedText.substring(7, encodedText.length() - 2);
            encodedText = encodedText.replaceAll(" ", "\\\\040");
            encodedText = encodedText.replaceAll("!", "\\\\041");
            encodedText = encodedText.replaceAll("\\.", "\\\\056");
            encodedText = encodedText.replaceAll("\\?", "\\\\077");
            encodedText = encodedText.replaceAll("\\,", "\\\\054");

            // 8진수 ASCII 문자열을 바이트 배열로 변환
            String[] octalBytes = encodedText.split("\\\\");
            byte[] bytes = new byte[octalBytes.length - 1];
            for (int i = 1; i < octalBytes.length; i++) {
                bytes[i - 1] = (byte) Integer.parseInt(octalBytes[i], 8);
            }

            decodedText = new String(bytes, StandardCharsets.UTF_8);

            System.out.println("======================");
            System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
            System.out.format("Detected Intent: %s (confidence: %f)\n",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
            System.out.format("Fulfillment Text: '%s'\n", decodedText);

            if (queryResult.getIntent().getDisplayName().equals("Default Fallback Intent"))
                return "fallback";
            else if (queryResult.getIntent().getDisplayName().equals("Default Welcome Intent"))
                return "fallback";
        }
        item.put("question", question);
        item.put("answer", decodedText);
        item.put("isDialogflow", true);
        item.put("modifyRequest", false);
        item.put("modifyText", "");
        array.add(item);

        update.push("contextUser").each(array);
        mongoTemplate.updateFirst(query, update, "ChatRoom");

        return decodedText;
    }

    // get answer from ChatGPT
    @Override
    public String processSearch(String chatId, String question) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(new ObjectId(chatId)));

        ChatGPTRequest chatGPTRequest = new ChatGPTRequest();
        chatGPTRequest.setPrompt(question);

        String url = OPEN_AI_URL;

        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Bearer " + OPEN_AI_KEY);

        Gson gson = new Gson();

        String body = gson.toJson(chatGPTRequest);

        log.info("body: " + body);

        try {
            final StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
            post.setEntity(entity);

            try (CloseableHttpClient httpClient = HttpClients.custom().build();
                 CloseableHttpResponse response = httpClient.execute(post)) {

                String responseBody = EntityUtils.toString(response.getEntity());

                log.info("responseBody: " + responseBody);

                ChatGPTResponse chatGPTResponse = gson.fromJson(responseBody, ChatGPTResponse.class);

                String answer = "답변을 찾을 수 없어 ChatGPT 한테 물어보았습니다\n\n" + chatGPTResponse.getChoices().get(0).getText();

                item.put("question", question);
                item.put("answer", answer);
                item.put("isDialogflow", false);
                item.put("modifyRequest", false);
                item.put("modifyText", "");
                array.add(item);

                update.push("contextUser").each(array);
                mongoTemplate.updateFirst(query, update, "ChatRoom");

                return answer;
            } catch (Exception e) {
                return "failed";
            }
        } catch (Exception e) {
            return "failed";
        }
    }

    // find newest chatRoomObjectId
    @Override
    public String findChatRoomObjectId(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getChatRoomOid() != null) {
            return user.getChatRoomOid().get(user.getChatRoomOid().size() - 1);
        } else {
            return "false"; // chatroom이 없어 null 일 경우 새채팅방 생성 후 리턴해야함 일단 false
        }
    }

    // get ChatRoom Data of Requested ChatRoomObjectId
    @Override
    public ChatRoom findById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).get();
    }

    // Request to modify wrong information
    @Override
    public String modifyInformation(ModifyRequestDto modifyRequestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(modifyRequestDto.get_id()).orElse(null);

        if (chatRoom != null) {
            List<Object> contextUser = chatRoom.getContextUser();
            if (contextUser != null) {

                @SuppressWarnings("unchecked")
                Map<String, Object> context = (Map<String, Object>) contextUser.get(modifyRequestDto.getContextIndex());

                context.put("modifyRequest", true);
                context.put("modifyText", modifyRequestDto.getText());
            }
            chatRoomRepository.save(chatRoom);

            return "success";
        } else {
            return "failed";
        }
    }
}
