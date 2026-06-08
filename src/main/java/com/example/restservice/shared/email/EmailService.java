package com.example.restservice.shared.email;

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

    
}
