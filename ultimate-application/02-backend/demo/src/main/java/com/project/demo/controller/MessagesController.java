package com.project.demo.controller;

import com.project.demo.entity.Message;
import com.project.demo.requestmodels.AdminQuestionRequest;
import com.project.demo.service.MessagesService;
import com.project.demo.utils.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class MessagesController {

    private MessagesService messagesService;

    @PostMapping("/secure/add/message")
    public void postMessage(@RequestHeader(value = "Authorization") String token, @RequestBody Message messageRequest){
        String userEmail = JWTUtil.payloadJWTExtraction(token, "\"sub\"");
        messagesService.postMessage(messageRequest, userEmail);
    }

    @PutMapping("/secure/admin/message")
    public void putMessage(@RequestHeader(value = "Authorization") String token, @RequestBody AdminQuestionRequest adminQuestionRequest)
            throws Exception{
        String userEmail = JWTUtil.payloadJWTExtraction(token, "\"sub\"");
        String admin = JWTUtil.payloadJWTExtraction(token, "\"userType\"");
        if (admin == null || !admin.equals("admin")){
            throw new Exception("Administration page only.");
        }
        messagesService.putMessage(adminQuestionRequest, userEmail);
    }

}
