# Android Contact Manager App 📱

## Description

Une application Android développée en Java qui permet de :

- Récupérer automatiquement tous les contacts du téléphone.
- Envoyer automatiquement les nouveaux contacts ajoutés vers un backend Spring Boot.
- Afficher les contacts sous une interface moderne et animée.
- Ouvrir un dialogue personnalisé avec des options :
    - Appeler un contact
    - Envoyer un SMS
- Filtrer et rechercher les contacts facilement.

---

## Fonctionnalités 🛠️

### Android App :
- Design Moderne (Material UI)
- Animation Slide sur les items
- Récupération automatique IMEI ou ANDROID_ID
- Insertion des nouveaux contacts vers Backend
- Vérification des doublons avant insertion
- Recherche des contacts
- Auto Observer pour détecter les nouveaux contacts
- Popup personnalisé pour Call & SMS

### Backend (Spring Boot):
- API REST CRUD
- Stockage des contacts dans MySQL
- Vérification si numéro existe déjà
- API sécurisée et optimisée

---

## Tech Stack 🚀

| Android        | Backend        | Base de données  |
|----------------|----------------|-----------------|
|Java / XML      |Java Spring Boot|MySQL            |
|Retrofit        |Spring Data JPA |                 |
|LiveData        |REST API        |                 |

---

## Installation

1. Clone le projet :
```bash
git clone https://github.com/ton-compte/ton-projet.git
```
2. Lancer le Backend :
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   ## Vidéo Démonstration 🎥

Clique ici pour voir la vidéo de démonstration :


https://github.com/user-attachments/assets/02681983-67ca-416e-91b3-9a45bd207b39



   
