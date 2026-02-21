# 📅 Digital Agenda - Full Stack Web Application

Benvenuto nel progetto **Digital Agenda**, un sistema completo per la gestione di appuntamenti e disponibilità, pensato per professionisti e lavoratori autonomi.

L'applicazione permette ai professionisti di configurare i propri slot orari e ai clienti di prenotare appuntamenti in modo asincrono e sicuro.

## 🚀 Architettura del Progetto

Il progetto adotta un'architettura moderna a **microservizi** e una comunicazione **event-driven**:

- **Monolite Backend (Spring Boot):** Gestisce la logica di business core (utenti, orari, appuntamenti) e la sicurezza JWT.
- **Notification Service (Spring Boot):** Microservizio dedicato all'invio automatico di email.
- **Message Broker (RabbitMQ):** Gestisce la comunicazione asincrona tra il monolite e il servizio notifiche per garantire scalabilità e resilienza.
- **Frontend (Angular 18):** Interfaccia utente moderna e reattiva sviluppata con componenti Standalone.
- **Database (MySQL):** Persistenza dei dati.

## 🛠️ Tech Stack

- **Backend:** Java 17/21, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.
- **Frontend:** Angular 18, TypeScript, Tailwind CSS / Bootstrap.
- **Infrastruttura:** Docker, RabbitMQ, MySQL.
- **Documentazione:** Swagger/OpenAPI.

## 📋 Prerequisiti

- Docker & Docker Compose
- Java 17+
- Node.js & Angular CLI

## 🏁 Come avviare il progetto

### 1. Clona il repository
```bash
git clone https://github.com/claudevlab/digital-agenda.git
cd digital-agenda

-----------------------------ENG---------------------------------------------------

# 📅 Digital Agenda - Full Stack Web Application

Welcome to the **Digital Agenda** project, a comprehensive appointment and availability management system tailored for professionals and freelancers.

The application enables professionals to configure their availability slots and allows clients to book appointments through a secure, asynchronous workflow.

## 🚀 Project Architecture

The project leverages a modern **microservices** architecture and **event-driven** communication:

- **Backend Monolith (Spring Boot):** Manages core business logic (users, schedules, appointments) and JWT-based security.
- **Notification Service (Spring Boot):** A dedicated microservice focused exclusively on automated email delivery.
- **Message Broker (RabbitMQ):** Handles asynchronous communication between the monolith and the notification service to ensure scalability and system resilience.
- **Frontend (Angular 18):** A modern, reactive user interface developed using Standalone components.
- **Database (MySQL):** Reliable data persistence.

## 🛠️ Tech Stack

- **Backend:** Java 17/21, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.
- **Frontend:** Angular 18, TypeScript, Tailwind CSS / Bootstrap.
- **Infrastructure:** Docker, RabbitMQ, MySQL.
- **Documentation:** Swagger/OpenAPI.

## 📋 Prerequisites

- Docker & Docker Compose
- Java 17+
- Node.js & Angular CLI

## 🏁 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/claudevlab/digital-agenda.git
cd digital-agenda

