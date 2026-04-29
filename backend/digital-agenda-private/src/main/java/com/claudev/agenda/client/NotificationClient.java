/*
//  SENZA RABBITMQ
package com.claudev.agenda.client;


import com.claudev.agenda.dto.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class NotificationClient {

    private final RestTemplate restTemplate;
    public  static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    // url del microservice : (per ora hardcoded poi lo mettero' nell app properties TO DO)
    private final String notificationServiceUrl = "http://localhost:8081/api/notifications/email";

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmailNotification (String to, String subject,String body) {

        //crea oggetto richiesta
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO (to,subject,body);

        try {
            /* restTemplate --> e' ilcorriere che trasporta dati dal microservices
             notificationServiceUrl --> l'indirizzo
             emailRequestDTO --> e il pacco
             String.class --> vuol dire restituiscimi il contenuto sottoforma di testo

            restTemplate.postForObject(notificationServiceUrl,emailRequestDTO,String.class);
            logger.info(" Notifica inviata al microservizio per: {} " , to);

        } catch (Exception exception) {
            logger.info("Errore invio notifica: {} " , exception.getMessage());
            //  se dovesse fallire l'invio di un email non si blocca l'app
        }
    }
}
*/