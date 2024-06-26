package com.example.smacsocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccessLogService {
    private Logger logger = LoggerFactory.getLogger(AccessLogService.class);

    @Value("${alert-server.address}")
    private String alertServerAddress;

    public String sendPostRequest(String alertNo){
        String url=alertServerAddress+"/post?alertNo="+alertNo;

        RestTemplate restTemplate=new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.POST,null,String.class);

        HttpStatusCode status =response.getStatusCode();
        String body=response.getBody();

        logger.info("url:"+url+" status:"+status.toString()+" body:"+body);

        return body;
    }
}
