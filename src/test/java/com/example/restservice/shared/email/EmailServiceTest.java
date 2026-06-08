package com.example.restservice.shared.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/** Tests unitaires de l'envoi d'emails (Phase 2 — bloc 6). */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender mailSender;

    @Test
    void envoie_la_confirmation_de_commande_quand_active() {
        EmailService service = new EmailService(mailSender, true, "from@demo.com");

        service.sendOrderConfirmation("client@demo.com", 5L, new BigDecimal("40.00"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("client@demo.com");
        assertThat(message.getFrom()).isEqualTo("from@demo.com");
        assertThat(message.getSubject()).contains("#5");
    }

    @Test
    void n_envoie_rien_quand_desactive() {
        EmailService service = new EmailService(mailSender, false, "from@demo.com");

        service.sendOrderConfirmation("client@demo.com", 5L, BigDecimal.TEN);

        verifyNoInteractions(mailSender);
    }

    @Test
    void n_envoie_rien_sans_destinataire() {
        EmailService service = new EmailService(mailSender, true, "from@demo.com");

        service.sendOrderConfirmation(null, 5L, BigDecimal.TEN);

        verifyNoInteractions(mailSender);
    }

    @Test
    void un_echec_smtp_n_est_pas_propage() {
        EmailService service = new EmailService(mailSender, true, "from@demo.com");
        doThrow(new MailSendException("SMTP indisponible"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatCode(() -> service.sendPaymentReceipt("client@demo.com", 5L, BigDecimal.TEN, "REF-1"))
                .doesNotThrowAnyException();
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
