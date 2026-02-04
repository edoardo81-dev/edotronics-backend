## README — BACKEND (`edotronics-backend`)

# Edotronics — Backend (Spring Boot)

Backend della demo **Edotronics**, un progetto didattico tipo e-commerce/gestionale che mette in pratica:
- relazioni bidirezionali tra entità (JPA/Hibernate)
- API REST complete
- sicurezza con **JWT**
- ruoli e profili applicativi
- eventi realtime (SSE)

🌐 **API Live (Render):** https://edotronics-backend.onrender.com  
🔗 **Frontend (Render):** https://edotronics-frontend.onrender.com  

---

## Funzionalità principali

### API Pubbliche
- catalogo prodotti pubblico
- ping pubblico per keep-alive (UptimeRobot)
- endpoint SSE pubblico per eventi inventario

### Auth & Sicurezza
- login / register
- emissione token **JWT**
- filtro `JwtAuthFilter` che valida token e popola lo `SecurityContext`
- gestione errori 401/403 con JSON
- ruoli: `USER`, `ADMIN` con protezione delle route

### Area USER (`/api/me/**`)
- profilo utente
- cambio password
- creazione ordini e storico “I miei ordini”

### Area ADMIN (`/api/admin/**`)
- CRUD prodotti
- gestione ordini
- gestione promozioni
- gestione stock alerts
- statistiche / vendite (se previste dal progetto)

### Realtime
- **Server-Sent Events (SSE)**: quando cambia l’inventario (ordine creato, CRUD prodotti, restock) il backend invia evento al client.

---

## Stack Tecnologico

- **Java 17**
- **Spring Boot 3.x**
- **Spring Web (REST)**
- **Spring Data JPA (Hibernate)**
- **Spring Security**
- **JWT (jjwt)**
- **Validation (Jakarta Validation)**
- DB:
  - **H2** (profilo `demo`)
  - **MySQL** (profilo `prod`)
- Deploy: **Docker + Render**

---

## Profili e configurazione

### Profilo `demo` (sviluppo)
- DB: H2 in-memory
- seed automatico dati (DataInitializer)

Esecuzione:
```bash
SPRING_PROFILES_ACTIVE=demo
Profilo prod (produzione)
DB: MySQL (config da env vars su Render)

Esecuzione:

SPRING_PROFILES_ACTIVE=prod
Variabili ambiente (Render)
Obbligatorie:

SPRING_PROFILES_ACTIVE = demo (se vuoi H2 + seed) oppure prod

APP_JWT_SECRET = stringa >= 32 caratteri (chiave JWT)

Opzionali (prod/MySQL):

DB_URL

DB_USERNAME

DB_PASSWORD

Nota: server.port=${PORT:8080} già pronto per Render.

Endpoint Keep-Alive (UptimeRobot)
Per evitare lo sleep su Render free tier, puoi pingare periodicamente:

GET https://edotronics-backend.onrender.com/api/public/ping

Risposta tipica:

{
  "at": "...",
  "service": "userorder-backend",
  "ok": true
}
Avvio in locale (senza Docker)
mvn clean package
mvn spring-boot:run -Dspring-boot.run.profiles=demo
Docker (build & run)
Build:

docker build -t edotronics-backend .
Run (demo):

docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=demo \
  -e APP_JWT_SECRET="12345678901234567890123456789012" \
  edotronics-backend
Link utili
Backend (live): https://edotronics-backend.onrender.com

Frontend (live): https://edotronics-frontend.onrender.com
