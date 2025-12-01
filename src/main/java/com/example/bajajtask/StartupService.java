package com.example.bajajtask;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.bajajtask.dto.FinalQueryRequest;
import com.example.bajajtask.dto.GenerateWebhookRequest;
import com.example.bajajtask.dto.GenerateWebhookResponse;

@Component
public class StartupService implements CommandLineRunner {

    private final RestTemplate restTemplate;

    public StartupService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) {

        try {
            // STEP 1 – Call Generate Webhook API
            String generateUrl =
                    "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            GenerateWebhookRequest request = new GenerateWebhookRequest(
                    " P sai kaushik",
                    "22BCE8366",
                    "kaushikpenjuri@gmail.com"
            );

            GenerateWebhookResponse response =
                    restTemplate.postForObject(
                            generateUrl,
                            request,
                            GenerateWebhookResponse.class
                    );

            if (response == null) {
                System.out.println("❌ No response from webhook API");
                return;
            }

            System.out.println("✅ Webhook URL: " + response.getWebhook());
            System.out.println("✅ Access Token: " + response.getAccessToken());

            // STEP 2 – Prepare SQL body
            FinalQueryRequest finalQuery =
                    new FinalQueryRequest(SqlConstants.FINAL_QUERY);

            // STEP 3 – Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", response.getAccessToken());

            HttpEntity<FinalQueryRequest> entity =
                    new HttpEntity<>(finalQuery, headers);

            // STEP 4 – POST final SQL
            ResponseEntity<String> submitResponse =
                    restTemplate.postForEntity(
                            response.getWebhook(),
                            entity,
                            String.class
                    );

            System.out.println("✅ FINAL SUBMIT STATUS: " +
                    submitResponse.getStatusCode());

            System.out.println("✅ FINAL RESPONSE BODY: " +
                    submitResponse.getBody());

        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
