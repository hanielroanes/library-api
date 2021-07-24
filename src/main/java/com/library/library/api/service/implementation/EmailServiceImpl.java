package com.library.library.api.service.implementation;

import com.library.library.api.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${application.mail.default-remetente}")
    private String remetent;

    @Override
    public void sendMails(String message, List<String> listEmails) {
        String[] mails = listEmails.toArray(new String[listEmails.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("Livro com emprestimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(mails);

        mailSender.send(mailMessage);
    }
}
