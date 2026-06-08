package com.example.restservice.shared.email;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Envoi d'emails transactionnels (confirmation de commande, reçu de paiement).
 *
 * <p>Les méthodes sont {@link Async} : l'envoi se fait hors du thread de la requête et
 * <b>hors transaction</b> — d'où des paramètres primitifs (jamais d'entité JPA lazy).
 * Tout échec SMTP est journalisé puis ignoré : un email raté ne doit jamais faire
 * échouer une commande ou un paiement.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public EmailService(JavaMailSender mailSender,
            @Value("${app.mail.enabled:true}") boolean enabled,
            @Value("${app.mail.from:no-reply@plateforme-service.local}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    @Async
    public void sendOrderConfirmation(String to, Long orderId, BigDecimal total) {
        send(to,
                "Confirmation de votre commande #" + orderId,
                "Bonjour,\n\nVotre commande #" + orderId + " a bien été enregistrée.\n"
                        + "Montant total : " + total + " €.\n\n"
                        + "Merci pour votre achat !");
    }

    @Async
    public void sendPaymentReceipt(String to, Long orderId, BigDecimal amount, String transactionRef) {
        send(to,
                "Reçu de paiement — commande #" + orderId,
                "Bonjour,\n\nNous confirmons le paiement de votre commande #" + orderId + ".\n"
                        + "Montant : " + amount + " €.\n"
                        + "Référence de transaction : " + transactionRef + "\n\n"
                        + "Merci pour votre confiance.");
    }

    private void send(String to, String subject, String body) {
        if (!enabled) {
            log.debug("Envoi d'email désactivé (app.mail.enabled=false) — destinataire {}", to);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Email non envoyé : destinataire absent (sujet : {})", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email envoyé à {} : {}", to, subject);
        } catch (MailException ex) {
            log.warn("Échec de l'envoi d'email à {} ({}) : {}", to, subject, ex.getMessage());
        }
    }
}
