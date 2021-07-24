package com.library.library.api.service;

import java.util.List;

public interface EmailService {
    void sendMails(String message, List<String> listEmails);
}
