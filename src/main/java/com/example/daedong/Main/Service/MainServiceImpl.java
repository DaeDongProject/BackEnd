package com.example.daedong.Main.Service;

import com.example.daedong.Dto.*;
import com.example.daedong.Main.Repository.ChatRoomRepository;
import com.example.daedong.Main.Repository.UserRepository;
import com.example.daedong.Menu.Controller.MenuController;
import com.example.daedong.Menu.Service.MenuServiceImpl;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.dialogflow.v2.*;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    @Value("${OPEN_AI_URL}")
    private String OPEN_AI_URL;

    @Value("${OPEN_AI_KEY}")
    private String OPEN_AI_KEY;

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MenuController menuController;

    Update update = new Update();
    org.bson.Document item = new org.bson.Document();

    // get answer from DialogFlow
    @Override
    public String detectIntentTexts(String chatId, String projectId, String question, String sessionId, String languageCode) throws IOException, ApiException {

        Query query = new Query().addCriteria(Criteria.where("_id").is(new ObjectId(chatId)));

        QueryResult queryResult;
        String decodedText;
        List<Document> array = new ArrayList<>();

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

            System.out.println(encodedText);
            System.out.println("================");

            ArrayList<String> arrayList = encodingTexts(encodedText);

            // 8진수 유니코드를 바이트 배열로 변환
            byte[] bytes = new byte[arrayList.size() - 1];
            for (int i = 1; i < arrayList.size(); i++) {
                bytes[i - 1] = (byte) Integer.parseInt(arrayList.get(i), 8);
            }

            // 바이트 배열을 String 형식으로 파싱
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
        mongoTemplate.upsert(query, update, "ChatRoom");

        return decodedText;
    }

    @Override
    public ArrayList<String> encodingTexts(String encodedText) {

        encodedText = encodedText.replaceAll(" ", "\\\\040");               // 공백을 인코딩
        encodedText = encodedText.replaceAll("!", "\\\\041");               // !
        encodedText = encodedText.replaceAll("\"", "\\042");                // "
        encodedText = encodedText.replaceAll("%", "\\\\045");               // %
        encodedText = encodedText.replaceAll("&", "\\\\046");               // &
        encodedText = encodedText.replaceAll("'", "\\\\047");               // '
        encodedText = encodedText.replaceAll("\\(", "\\\\050");             // (
        encodedText = encodedText.replaceAll("\\)", "\\\\051");             // )
        encodedText = encodedText.replaceAll("\\*", "\\\\052");             // *
        encodedText = encodedText.replaceAll("\\+", "\\\\053");             // +
        encodedText = encodedText.replaceAll(",", "\\\\054");               // ,
        encodedText = encodedText.replaceAll("-", "\\\\055");               // -
        encodedText = encodedText.replaceAll("\\.", "\\\\056");             // .
        encodedText = encodedText.replaceAll("/", "\\\\057");               // /
        encodedText = encodedText.replaceAll(":", "\\\\072");               // :
        encodedText = encodedText.replaceAll("<", "\\\\074");               // <
        encodedText = encodedText.replaceAll("=", "\\\\075");               // =
        encodedText = encodedText.replaceAll(">", "\\\\076");               // >
        encodedText = encodedText.replaceAll("\\?", "\\\\077");             // ?
        encodedText = encodedText.replaceAll("\\[", "\\\\133");             // [
        encodedText = encodedText.replaceAll("]", "\\\\135");               // ]
        encodedText = encodedText.replaceAll("\\\\n", "\\\\134\\\\156");    // \\n
        encodedText = encodedText.replaceAll("~", "\\\\176");               // ~

        // 알파벳 인코딩 추가
        for (char c = 'A'; c <= 'Z'; c++) {
            encodedText = encodedText.replaceAll(Character.toString(c), convertToUnicodeOctal(c));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            encodedText = encodedText.replaceAll(Character.toString(c), convertToUnicodeOctal(c));
        }

        System.out.println(encodedText);
        System.out.println("=====================");

        String[] octalArray = encodedText.split("\\\\");
        ArrayList<String> arrayList = new ArrayList<>();

        for (String s : octalArray) {
            if (s.length() > 3) {
                arrayList.add(s.substring(0, 3));

                String newStr = s.substring(3);
                for (int j = 0; j < newStr.length(); j++) {
                    String inputStr = "";

                    if (newStr.charAt(j) == '0')
                        inputStr = "060";
                    else if (newStr.charAt(j) == '1')
                        inputStr = "061";
                    else if (newStr.charAt(j) == '2')
                        inputStr = "062";
                    else if (newStr.charAt(j) == '3')
                        inputStr = "063";
                    else if (newStr.charAt(j) == '4')
                        inputStr = "064";
                    else if (newStr.charAt(j) == '5')
                        inputStr = "065";
                    else if (newStr.charAt(j) == '6')
                        inputStr = "066";
                    else if (newStr.charAt(j) == '7')
                        inputStr = "067";
                    else if (newStr.charAt(j) == '8')
                        inputStr = "070";
                    else if (newStr.charAt(j) == '9')
                        inputStr = "071";

                    arrayList.add(inputStr);
                }
            } else {
                arrayList.add(s);
            }
        }
        System.out.println(arrayList);
        System.out.println("================");

        return arrayList;
    }

    @Override
    // 유니코드를 8진수로 변환하는 메서드
    public String convertToUnicodeOctal(char character) {
        return convertToOctalString(character);
    }

    public String convertToOctalString(int value) {
        return "\\\\" + Integer.toOctalString(value);
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

        List<Document> array = new ArrayList<>();

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
                mongoTemplate.upsert(query, update, "ChatRoom");

                return answer;
            } catch (Exception e) {
                return "failed";
            }
        } catch (Exception e) {
            return "failed";
        }
    }

    // 해당 유저의 가장 최근 chatRoomObjectId 리턴
    @Override
    public String findChatRoomObjectId(String userId) throws NullPointerException {

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {

            if (user.getChatRoomOid().size() == 0) {
                return menuController.CreateChatRoom(user); // 유저가 존재하고, 채팅방이 존재하지 않으면 새 채팅방 생성 후 Oid 반환
            } else {
                ChatRoom chatRoom = new ChatRoom();

                for (int i = 1; i <= user.getChatRoomOid().size(); i++) {

                    chatRoom = chatRoomRepository.findById(user.getChatRoomOid().get(user.getChatRoomOid().size() - i)).get();

                    if (!chatRoom.isDeleteYn()){    // 유저가 존재하고, 채팅방이 존재하면 최근에 생성한 채팅방 Oid 반환 + deleteYn이 false이어야 함
                        break;
                    }
                }
                return chatRoom.get_id();
            }
        } else {
            return "failed";
        }
    }

    // 요청받은 ChatRoomObjectId의 채팅 데이터를 반환
    @Override
    public ChatRoom findById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).get();
    }

    // 잘못된 정보를 수정하기 위한 요청
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
