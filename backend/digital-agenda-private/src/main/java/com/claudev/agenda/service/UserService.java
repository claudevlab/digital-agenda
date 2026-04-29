package com.claudev.agenda.service;

import com.claudev.agenda.client.RabbitMQProducer;
import com.claudev.agenda.dto.EmailRequestDTO;
import com.claudev.agenda.dto.UpgradeProfessionalDTO;
import com.claudev.agenda.dto.UserProfessionalResponseDTO;
import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.EmailType;
import com.claudev.agenda.enums.Role;
import com.claudev.agenda.mapper.UserMapper;
import com.claudev.agenda.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQProducer rabbitMQProducer;

    public UserService(UserRepository userRepository, UserMapper userMapper,PasswordEncoder passwordEncoder,RabbitMQProducer rabbitMQProducer) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // utile per i test iniziali
    @Transactional
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // preparazione della email di conferma registrazione
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setTo(savedUser.getEmail());
        emailRequestDTO.setSubject("Benvenuto in Digital Agenda - Registrazione Confermata");
        emailRequestDTO.setEmailType(EmailType.REGISTRATION_CONFIRMED);

        // Riempiamo i campi dinamici per il template Thymeleaf
        // Usiamo il nome dell'utente appena registrato. Puoi usare customerName o professionalName
        emailRequestDTO.setCustomerName(savedUser.getFirstName() + " " + savedUser.getLastName());

        // Impostiamo l'URL dell'applicazione (front-end)
        emailRequestDTO.setAppUrl("http://localhost:4200/login");

        rabbitMQProducer.sendEmailNotification(emailRequestDTO);
        return savedUser;
    }

    // per il login con OAuth2
    // invia l'email anche se con accesso OAuth di google
    @Transactional
    public User processOAuthPostLogin (String email, String firstName,String lastName) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Google non ha fornito un indirizzo email valido.");
        }

        // findByEmail cerca l'utente.
        // Se lo trova, lo restituisce e BASTA (niente email di benvenuto).
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Se NON lo trova, entra in questo blocco: significa che è la PRIMA VOLTA!

                    // 1. Creiamo il DTO per usare MapStruct (opzionale, ma pulito)
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setFirstName((firstName != null && !firstName.isBlank()) ? firstName : "Utente");
                    newUser.setLastName((lastName != null && !lastName.isBlank()) ? lastName : "Google");
                    newUser.setRole(Role.CUSTOMER);
                    newUser.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));

                    // 2. Salviamo l'utente
                    User savedUser = userRepository.save(newUser);

                    // 3. CREAZIONE DEL DTO PER RABBITMQ
                    EmailRequestDTO emailRequest = new EmailRequestDTO();
                    emailRequest.setTo(savedUser.getEmail());
                    emailRequest.setSubject("Benvenuto in Agenda Digitale!");
                    emailRequest.setEmailType(EmailType.REGISTRATION_CONFIRMED);

                    // Campi dinamici per Thymeleaf (adattali a come è fatto il tuo template "registration-confirmed.html")
                    emailRequest.setCustomerName(savedUser.getFirstName());
                    emailRequest.setAppUrl("http://localhost:4200/login"); // Sostituisci con l'URL vero del frontend in prod

                    // 3. SPEDIZIONE ASINCRONA DELL'EVENTO AL MICROSERVIZIO!
                    rabbitMQProducer.sendEmailNotification(emailRequest);

                    return savedUser;
                });

    }


    public List<UserProfessionalResponseDTO> getAllProfessionals() {
        return userRepository.findByRole(Role.PROFESSIONAL).stream().map(userMapper::toProfessionalResponseDTO).toList();
    }

    public List <UserProfessionalResponseDTO> getProfessionals (String search) {
        List<User> users;

        if (search == null || search.trim().isEmpty() ) {
            users = userRepository.findByRole(Role.PROFESSIONAL);
        } else {
            users = userRepository.searchProfessionals(search.trim(),Role.PROFESSIONAL);
        }
        return users.stream().map(userMapper::toProfessionalResponseDTO).toList();
    }

    @Transactional
    public void generatePasswordResetToken (String email) {
        // 1 -  cerca l'utente
        User user = userRepository.findByEmail(email).orElseThrow( () -> new RuntimeException("Nessun utente trovato con questa email"));

        // 2 - Generiamo un token univoco e la scadenza 30 min
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));

        // salviamo l'utente aggiornato
        userRepository.save(user);

        // 3 - prepariamo l'email con il link di reset ( puntera' al frontend)
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("Richiesta di Reset Password - Agenda Digitale");
        emailRequest.setEmailType(EmailType.RESET_PASSWORD_REQUEST);
        emailRequest.setCustomerName(user.getFirstName()); // utilizziamo questo campo generico per il nome
        emailRequest.setAppUrl(resetLink); // il pulsante nella email portera' a questo link

        // 4 - Incolonniamo l'email in RabbotMQ
        rabbitMQProducer.sendEmailNotification(emailRequest);

    }

    @Transactional
    public void resetPassword (String token , String newPassword) {
        // 1. Cerchiamo l'utente tramite il token
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token non valido o inesistente."));

        // 2 - controlle del token che non sia scaduto
        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw  new RuntimeException("Il link di reset è scaduto. Richiedine uno nuovo.");
        }

        // 3 - aggiorniamo la password criptandola
        user.setPassword(passwordEncoder.encode(newPassword));

        // 4 - invalidiamo il token per impedire che venga riutilizzato
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        // salvataggio
        userRepository.save(user);

        // --->  Invia l'email di conferma <---
        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("La tua password è stata modificata");
        emailRequest.setEmailType(EmailType.PASSWORD_CHANGED_SUCCESS);
        emailRequest.setCustomerName(user.getFirstName());
        // Non serve l'appUrl per un reset, o se vuoi puoi metterlo per farli loggare
        emailRequest.setAppUrl("http://localhost:4200/login");

        rabbitMQProducer.sendEmailNotification(emailRequest);

    }

    // cliente che vuol diventare professionista
    @Transactional
    public User upgradeToProfessional (String email , UpgradeProfessionalDTO upgradeProfessionalDTO) {

        // recupera utente
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Controllo che non sia gia' un professinista
        if (user.getRole() == Role.PROFESSIONAL) {
            throw new RuntimeException("Sei gia' un professionista");
        }

        // aggiorna i dati
        // MAGIA DI MAPSTRUCT: Imposta i campi e cambia il ruolo automaticamente!
        userMapper.upgradeUserFromDto(upgradeProfessionalDTO, user);

        // IMPORTANTE : cambia ruolo
        user.setRole(Role.PROFESSIONAL);

        // Salvataggio
        return userRepository.save(user);
    }


    // serve a far inserire ad ogni User il contatto telefonico . Necessario per il business
    @Transactional
    public User updatePhoneNumber(String email, String phoneNumber) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPhoneNumber(phoneNumber);
        return userRepository.save(user);
    }

    // professionista che vuol aggiornare il suo profilo TO DO
    @Transactional
    public User upgradeProfessionalDTO ( String email ,UpgradeProfessionalDTO upgradeProfessionalDTO) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("Utente non trovato"));

        if (user.getRole() != Role.PROFESSIONAL ) {
            throw new RuntimeException("Solo i professionisti possono avere un profilo aziendale");
        }

        // MAGIA DI MAPSTRUCT: Aggiorna l'oggetto 'user' con i dati del 'dto'
        userMapper.updateProfessionalFromDto(upgradeProfessionalDTO, user);

        return userRepository.save(user);
    }





}
