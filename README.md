# 📅 Digital Agenda - Full Stack Web Application

> 🌐 **Demo live:** [https://digital-agenda.it](https://digital-agenda.it)

Benvenuto nel progetto **Digital Agenda**, un sistema completo per la gestione di appuntamenti e disponibilità, pensato per professionisti e lavoratori autonomi.

L'applicazione permette ai professionisti di configurare i propri slot orari e ai clienti di prenotare appuntamenti in modo asincrono e sicuro. Il progetto è stato realizzato interamente in autonomia — dalla progettazione dell'architettura fino al deploy in produzione su VPS.

***

## 🚀 Architettura del Progetto

Il progetto adotta un'architettura moderna a **microservizi** con comunicazione **event-driven**:

- **Backend Monolite (Spring Boot):** Gestisce la logica di business core (utenti, orari, appuntamenti) e la sicurezza tramite JWT.
- **Notification Service (Spring Boot):** Microservizio dedicato esclusivamente all'invio automatico di email transazionali.
- **Message Broker (RabbitMQ):** Gestisce la comunicazione asincrona tra il monolite e il servizio di notifiche, garantendo scalabilità e resilienza.
- **Frontend (Angular 18):** Interfaccia utente moderna e reattiva, sviluppata con componenti Standalone.
- **Database (MySQL):** Persistenza relazionale dei dati.
- **Reverse Proxy (Traefik + Nginx):** Gestione del routing, terminazione SSL e serve dei file statici.
- **Monitoring (Prometheus + Grafana):** Stack di osservabilità per il monitoraggio delle metriche applicative e di sistema.

***

## ✨ Funzionalità Principali

- **Autenticazione dual-mode:** Login tradizionale (email/password) e accesso tramite Google via **OAuth2**
- **Sicurezza:** Gestione autenticazione e autorizzazione con **Spring Security** e token **JWT**
- **Dashboard professionista:** Configurazione degli slot orari, gestione appuntamenti e impostazione dei giorni di indisponibilità
- **Dashboard cliente:** Ricerca professionisti, prenotazione appuntamenti e storico prenotazioni
- **Notifiche email automatiche:** Inviate in modo asincrono tramite microservizio dedicato e RabbitMQ
- **API documentate:** Integrazione **Swagger / OpenAPI** per la documentazione degli endpoint REST
- **Deploy in produzione:** Applicazione live su VPS con container Docker e CI/CD via GitHub Actions

***

## 🛠️ Tech Stack

| Layer | Tecnologie |
|-------|-----------|
| **Backend** | Java 17/21, Spring Boot 3.x, Spring Security (JWT + OAuth2), Spring Data JPA, Hibernate, MapStruct |
| **Frontend** | Angular 18, TypeScript, SCSS |
| **Database** | MySQL |
| **Messaging** | RabbitMQ |
| **DevOps & Infrastruttura** | Docker, Docker Compose, GitHub Actions (CI/CD), Traefik, Nginx |
| **Monitoring** | Prometheus, Grafana |
| **Documentazione** | Swagger / OpenAPI |

***

## 📋 Prerequisiti

- Docker & Docker Compose
- Java 17+
- Node.js & Angular CLI

***

## 🏁 Come avviare il progetto

### 1. Clona il repository

```bash
git clone https://github.com/claudevlab/digital-agenda.git
cd digital-agenda
```

### 2. Avvia i container

```bash
docker-compose up --build
```

### 3. Accedi all'applicazione

- **Frontend:** `http://localhost:4200`
- **Backend API:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

***

## 🚀 Sviluppi Futuri & Roadmap

Per far evolvere l'ecosistema **Digital Agenda**, sono previste le seguenti funzionalità:

- **💳 Integrazione Pagamenti Online:** Implementazione degli SDK di **Stripe** o **PayPal** per consentire ai clienti di pagare gli appuntamenti direttamente durante la prenotazione.
- **📱 Applicazione Mobile:** Sviluppo di un'app nativa **Android / iOS** (con **Flutter** o **React Native**) per un'esperienza mobile ottimizzata sia per professionisti che per clienti.


## 👤 Autore

**Claudio Nugnes** — Junior Java Full Stack Developer
- 🔗 [LinkedIn](https://linkedin.com/in/claudio-nugnes)
- 🌐 [digital-agenda.it](https://digital-agenda.it)

***

-----------------------------ENG---------------------------------------------------

# 📅 Digital Agenda - Full Stack Web Application

> 🌐 **Live Demo:** [https://digital-agenda.it](https://digital-agenda.it)

Welcome to the **Digital Agenda** project, a comprehensive appointment and availability management system tailored for professionals and freelancers.

The application enables professionals to configure their availability slots and allows clients to book appointments through a secure, asynchronous workflow. The entire project — from architecture design to production deployment on a VPS — was built independently.

***

## 🚀 Project Architecture

The project leverages a modern **microservices** architecture with **event-driven** communication:

- **Backend Monolith (Spring Boot):** Manages core business logic (users, schedules, appointments) and JWT-based security.
- **Notification Service (Spring Boot):** A dedicated microservice focused exclusively on automated transactional email delivery.
- **Message Broker (RabbitMQ):** Handles asynchronous communication between the monolith and the notification service, ensuring scalability and resilience.
- **Frontend (Angular 18):** A modern, reactive user interface built with Standalone components.
- **Database (MySQL):** Reliable relational data persistence.
- **Reverse Proxy (Traefik + Nginx):** Handles routing, SSL termination, and static file serving.
- **Monitoring (Prometheus + Grafana):** Observability stack for application and system metrics.

***

## ✨ Key Features

- **Dual authentication:** Traditional login (email/password) and Google sign-in via **OAuth2**
- **Security:** Authentication and authorization managed with **Spring Security** and **JWT** tokens
- **Professional dashboard:** Schedule configuration, appointment management, and availability settings
- **Client dashboard:** Search for professionals, book appointments, and view booking history
- **Automated email notifications:** Sent asynchronously through a dedicated microservice via RabbitMQ
- **Documented REST API:** **Swagger / OpenAPI** integration for full endpoint documentation
- **Production deployment:** Live application on VPS with Docker containers and CI/CD via GitHub Actions

***

## 🛠️ Tech Stack

| Layer | Technologies |
|-------|-------------|
| **Backend** | Java 17/21, Spring Boot 3.x, Spring Security (JWT + OAuth2), Spring Data JPA, Hibernate, MapStruct |
| **Frontend** | Angular 18, TypeScript, SCSS |
| **Database** | MySQL |
| **Messaging** | RabbitMQ |
| **DevOps & Infrastructure** | Docker, Docker Compose, GitHub Actions (CI/CD), Traefik, Nginx |
| **Monitoring** | Prometheus, Grafana |
| **Documentation** | Swagger / OpenAPI |

***

## 📋 Prerequisites

- Docker & Docker Compose
- Java 17+
- Node.js & Angular CLI

***

## 🏁 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/claudevlab/digital-agenda.git
cd digital-agenda
```

### 2. Start the containers

```bash
docker-compose up --build
```

### 3. Access the application

- **Frontend:** `http://localhost:4200`
- **Backend API:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

***

## 🚀 Future Improvements & Roadmap

To further enhance the **Digital Agenda** ecosystem, the following features are planned:

- **💳 Online Payments Integration:** Implementation of **Stripe** or **PayPal** SDKs to allow customers to pay for appointments directly during the booking process.
- **📱 Mobile Application:** Development of a dedicated **Android / iOS** app (using **Flutter** or **React Native**) for a seamless mobile experience for both professionals and customers.

***

## 👤 Author

**Claudio Nugnes** — Junior Java Full Stack Developer
- 🔗 [LinkedIn](https://linkedin.com/in/claudio-nugnes)
- 🌐 [digital-agenda.it](https://digital-agenda.it)
