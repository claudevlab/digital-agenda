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
      Devi aggiungere @Mapping solo se un campo ha nome diverso tra l'entity e il DTO.
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

    UserResponseDTO toUserResponse(User user);

    UserProfessionalResponseDTO toProfessionalResponseDTO(User user);

    /*
  AGGIORNAMENTO PROFILO ESISTENTE (PATCH)
  Usiamo @MappingTarget per dire a MapStruct:
  "Prendi i dati dal DTO e mettili dentro questa Entity esistente".

  Importante: per la PATCH impostiamo nullValuePropertyMappingStrategy
  su IGNORE, così se dal frontend arriva un campo null (o non viene inviato),
  MapStruct non cancellerà il valore che c'è già nel database!
 */

    /*
   NOTA
   upgrade vs update professinal DTO avrei dovuto creare due uno per update e uno per upgrade ma sono uguali
   quindi anche se il nome e diverso ho deciso di riutilizzare lo stesso

    */

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "email", ignore = true)
    @org.mapstruct.Mapping(target = "password", ignore = true)
    @org.mapstruct.Mapping(target = "role", ignore = true)
    @org.mapstruct.Mapping(target = "username", ignore = true)
    @org.mapstruct.BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateProfessionalFromDto(UpgradeProfessionalDTO dto, @org.mapstruct.MappingTarget User user);



    /*
      UPGRADE A PROFESSIONISTA
      Stesso concetto: prendiamo un utente CUSTOMER esistente e gli
      aggiungiamo i campi del DTO dell'upgrade.
     */
    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "email", ignore = true)
    @org.mapstruct.Mapping(target = "password", ignore = true)
    @org.mapstruct.Mapping(target = "role", constant = "PROFESSIONAL") // Cambia il ruolo!
    @org.mapstruct.Mapping(target = "username", ignore = true)
    void upgradeUserFromDto(UpgradeProfessionalDTO dto, @org.mapstruct.MappingTarget User user);


}
