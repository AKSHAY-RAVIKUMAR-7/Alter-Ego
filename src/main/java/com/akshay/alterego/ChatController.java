package com.akshay.alterego;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private static final String OPENROUTER_API_KEY = "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";
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

    @PostMapping("/reply")
    public String getReply(@RequestBody List<Message> messages) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // Add system prompt
        List<Map<String, String>> fullMessages = new ArrayList<>();
        fullMessages.add(Map.of(
            "role", "system",
            "content", "You are the user's alter ego. Speak like their intelligent inner voice. Be smart, witty, and philosophical."
        ));

        for (Message msg : messages) {
            fullMessages.add(Map.of(
                "role", msg.getRole(),
                "content", msg.getContent()
            ));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-3.5-turbo"); // or "mistralai/mixtral-8x7b"
        body.put("messages", fullMessages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENROUTER_API_KEY);
        headers.set("HTTP-Referer", "https://alterego.local");  // REQUIRED
        headers.set("X-Title", "AlterEgo");                     // REQUIRED

        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(body), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENROUTER_URL, request, String.class);
            JsonNode root = mapper.readTree(response.getBody());

            return root.get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Error: " + e.getMessage();
        }
    }
}
