package com.notifplatform.emailworker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSender {

    @Value("${email.mock:true}")
    private boolean mockMode;

    @Value("${email.from}")
    private String fromAddress;

    public String send(String toEmail, String subject, String body) {
        if (mockMode) {
            log.info("[MOCK] email to={} subject='{}' body='{}'", toEmail, subject, body);
            return "mock-message-id-" + System.currentTimeMillis();
        }

        // should swap this block for SendGrid or AWS SES when ready
        // SendGrid example:
        //   SendGrid sg = new SendGrid(apiKey);
        //   Request request = new Request();
        //   request.setMethod(Method.POST);
        //   request.setEndpoint("mail/send");
        //   request.setBody(mail.build());
        //   Response response = sg.api(request);
        //   return response.getBody();

        throw new UnsupportedOperationException("real email provider not configured — set email.mock=true or wire a provider");
    }
}
