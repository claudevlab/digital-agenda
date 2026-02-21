package com.claudev.agenda.mapper;

import com.claudev.agenda.dto.*;
import com.claudev.agenda.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
MapStruct.
È molto più performante di ModelMapper (genera codice a compile-time, non usa reflection a runtime) ed è lo standard industriale oggi.
 */
@Mapper(componentModel = "spring")  // lo rendiamo un @component iniettabile
public interface UserMapper {


    /*
     @Mapping - > inserisci solo quei campi che hanno delle particolarita'/variazioni
     */
    @Mapping(target = "id", ignore = true) // per l'id che si auto-genera
    @Mapping(target = "username" , source = "email") // utilizziamo l'email come username ( non so ancora se lo modifico o lo lascio cosi)
    User toEntityCustomer(UsersRegistrationDTO usersRegistrationDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "email")
    @Mapping(target = "role", constant = "PROFESSIONAL") // Forziamo il ruolo!
    @Mapping(target = "paymentMethods", ignore = true)   // Ignoriamo liste complesse per ora
    User toEntityProfessional(UserProfessionalRegistrationDTO userProfessionalRegistrationDTO);

    @Mapping(target = "id", ignore = true) // per l'id che si auto-genera
    @Mapping(target = "password", constant = "")
    @Mapping(target = "username" , source = "email") // utilizziamo l'email come username ( non so ancora se lo modifico o lo lascio cosi)
    @Mapping(target = "role", constant = "CUSTOMER") // Ruolo default
    User toEntityFromOAuth2(String email, String firstname, String lastname);

    UserResponseDTO toUserReponse (User user);


}
