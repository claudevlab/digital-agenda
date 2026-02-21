package com.claudev.agenda.client;

import com.claudev.agenda.dto.EmailRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationClient {

    private final RestTemplate restTemplate;

    // url del microservice : (per ora hardcoded poi lo mettero' nell app properties TO DO)
    private final String notificationServiceUrl = "http://localhost:8081/api/notifications/email";

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmailNotification (String to, String subject,String body) {

        //crea oggetto richiesta
        // per semplicita' utilizzo un oggetto anonimo , l'ideale sarebbe un DTO ->TO DO

        EmailRequestDTO emailRequestDTO = new EmailRequestDTO (to,subject,body);

        try {
            /* restTemplate --> e' ilcorriere che trasporta dati dal microservices
             notificationServiceUrl --> l'indirizzo
             emailRequestDTO --> e il pacco
             String.class --> vuol dire restituiscimi il contenuto sottoforma di testo
             */
            restTemplate.postForObject(notificationServiceUrl,emailRequestDTO,String.class);
            System.out.println(" Notifica inviata al microservizio per: " + to);

        } catch (Exception exception) {
            System.err.println("Errore invio notifica: " + exception.getMessage());
            //  se dovesse fallire l'invio di un email non si blocca l'app
        }
    }
}
