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

        // Registration details
        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("name", "Abha Shukla");
        registerBody.put("regNo", "RA2211003011829");
        registerBody.put("email", "abha@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(registerBody, headers);

        // Call /generateWebhook
        ResponseEntity<String> response = restTemplate.postForEntity(registerUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            System.out.println("Initial request failed: " + response.getStatusCode());
            return;
        }

        // Parse the response
        JsonNode root = objectMapper.readTree(response.getBody());
        String webhookUrl = root.path("webhook").asText();
        String accessToken = root.path("accessToken").asText();
        JsonNode data = root.path("data");
        String regNo = registerBody.get("regNo");

        // Determine the question based on regNo
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));
        Map<String, Object> finalPayload = new HashMap<>();
        finalPayload.put("regNo", regNo);

        if (lastTwoDigits % 2 == 1) {
            // Solve Question 1: Mutual Followers
            JsonNode users = data.path("users");
            finalPayload.put("outcome", solveMutualFollowers(users));
        } else {
            // Solve Question 2: Nth-Level Followers
            int n = data.path("n").asInt();
            int findId = data.path("findId").asInt();
            JsonNode users = data.path("users");
            finalPayload.put("outcome", solveNthLevelFollowers(users, findId, n));
        }

        // Send the result to the webhook
        sendToWebhook(webhookUrl, accessToken, finalPayload);
    }

    private Set<List<Integer>> solveMutualFollowers(JsonNode users) {
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
        return result;
    }

    private Set<Integer> solveNthLevelFollowers(JsonNode users, int findId, int n) {
        Map<Integer, Set<Integer>> followMap = new HashMap<>();
        for (JsonNode user : users) {
            int id = user.path("id").asInt();
            Set<Integer> follows = new HashSet<>();
            for (JsonNode f : user.path("follows")) {
                follows.add(f.asInt());
            }
            followMap.put(id, follows);
        }

        Set<Integer> nthLevelFollowers = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(findId);

        int level = 0;
        while (!queue.isEmpty() && level < n) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                if (followMap.containsKey(current)) {
                    queue.addAll(followMap.get(current));
                }
            }
            level++;
        }

        nthLevelFollowers.addAll(queue);
        return nthLevelFollowers;
    }

    private void sendToWebhook(String webhookUrl, String accessToken, Map<String, Object> finalPayload) {
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