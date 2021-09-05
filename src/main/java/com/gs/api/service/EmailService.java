package com.gs.api.service;

import java.util.List;

public interface EmailService {
    void sendMails(String message, List<String> emails);
}
