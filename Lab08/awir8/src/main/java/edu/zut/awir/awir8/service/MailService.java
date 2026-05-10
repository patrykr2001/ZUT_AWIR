package edu.zut.awir.awir8.service;

import edu.zut.awir.awir8.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.admin:admin@example.com}")
    private String adminAddress;

    public void sendUserCreated(User user) {
        sendHtml(adminAddress, "Nowy uzytkownik zarejestrowany", "mail-user-created", user, "W systemie zarejestrowano nowego uzytkownika.");
        sendHtml(user.getEmail(), "Witaj w systemie", "mail-user-created", user, "Twoje konto zostalo utworzone.");
    }

    public void sendUserUpdated(User user) {
        sendHtml(adminAddress, "Dane uzytkownika zmodyfikowane", "mail-user-updated", user, "W systemie zmodyfikowano dane uzytkownika.");
        sendHtml(user.getEmail(), "Zmiana danych konta", "mail-user-updated", user, "Twoje dane zostaly zaktualizowane.");
    }

    private void sendHtml(String to, String subject, String template, User user, String intro) {
        if (to == null || to.isBlank()) {
            log.warn("Pominieto wysylke e-maila z powodu pustego adresata. Temat: {}", subject);
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("intro", intro);
            context.setVariable("user", user);

            String html = templateEngine.process(template, context);

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("Wyslano e-mail do {}: {}", to, subject);
        } catch (MailException | MessagingException ex) {
            log.error("Blad wysylania e-maila do {}: {}", to, ex.getMessage(), ex);
        }
    }
}
