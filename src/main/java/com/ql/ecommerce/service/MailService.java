package com.ql.ecommerce.service;

import org.springframework.stereotype.Service;

@Service
public interface MailService {
    void sendEmail(String to, String subject, String body);
}
