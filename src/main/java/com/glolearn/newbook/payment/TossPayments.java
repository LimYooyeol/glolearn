package com.glolearn.newbook.payment;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class TossPayments {

    // paymentKey 에 해당하는 결제 승인 요청
    public Boolean requireConfirm(String paymentKey, String orderId, Long amount){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("paymentKey", paymentKey);
        jsonObject.put("orderId", orderId);
        jsonObject.put("amount", amount);

        String response = null;
        try{
            response = WebClient.create("https://api.tosspayments.com")
                    .post()
                    .uri(
                            uriBuilder -> uriBuilder.path("/v1/payments/confirm").build()
                    )
                    .header("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                    .header("Content-Type", "application/json")
                    .body(BodyInserters.fromValue(jsonObject.toString()))
                    .retrieve()
                    .bodyToMono(String.class).block();
        }catch (WebClientResponseException e){
            throw new IllegalStateException("결제 실패");
        }

        JSONParser parser = new JSONParser();
        JSONObject result;
        try {
            result = (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return result.get("status").equals("DONE");
    }
}
