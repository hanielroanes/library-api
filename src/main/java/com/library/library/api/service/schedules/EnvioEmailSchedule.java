package com.library.library.api.service.schedules;

import com.library.library.api.model.Loan;
import com.library.library.api.service.EmailService;
import com.library.library.api.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvioEmailSchedule {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateloans.message}")
    private String message;

    @Autowired
    private LoanService loanService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public  void sendEmailToLateLoans(){
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> listEmails = allLateLoans.stream()
                .map(loan -> loan.getCustomerEmail()).collect(Collectors.toList());

        emailService.sendMails(message, listEmails );
    }
}
