package com.abha.mutualfollowers.MutualFollowers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class StartupRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        String registerUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("name", "Abha Shukla");
        registerBody.put("regNo", "RA2211003011829");
        registerBody.put("email", "abha@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(registerBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(registerUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println("Initial request failed: " + response.getStatusCode());
            return;
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        String webhookUrl = root.path("webhook").asText();
        String accessToken = root.path("accessToken").asText();
        JsonNode users = root.path("data").path("users");

        Map<Integer, Set<Integer>> followMap = new HashMap<>();
        for (JsonNode user : users) {
            int id = user.path("id").asInt();
            Set<Integer> follows = new HashSet<>();
            for (JsonNode f : user.path("follows")) {
                follows.add(f.asInt());
            }
            followMap.put(id, follows);
        }

        Set<List<Integer>> result = new HashSet<>();
        for (Map.Entry<Integer, Set<Integer>> entry : followMap.entrySet()) {
            int a = entry.getKey();
            for (int b : entry.getValue()) {
                if (followMap.containsKey(b) && followMap.get(b).contains(a) && a < b) {
                    result.add(Arrays.asList(a, b));
                }
            }
        }

        Map<String, Object> finalPayload = new HashMap<>();
        finalPayload.put("regNo", "RA2211003011829");
        finalPayload.put("outcome", result);

        HttpHeaders webhookHeaders = new HttpHeaders();
        webhookHeaders.setContentType(MediaType.APPLICATION_JSON);
        webhookHeaders.set("Authorization", accessToken);

        HttpEntity<Map<String, Object>> webhookRequest = new HttpEntity<>(finalPayload, webhookHeaders);

        for (int i = 0; i < 4; i++) {
            try {
                ResponseEntity<String> res = restTemplate.postForEntity(webhookUrl, webhookRequest, String.class);
                if (res.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Webhook sent successfully!");
                    break;
                } else {
                    System.out.println("Attempt " + (i + 1) + " failed: " + res.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("Attempt " + (i + 1) + " exception: " + e.getMessage());
            }
        }
    }
}