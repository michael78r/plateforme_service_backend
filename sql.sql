-- ============================================
-- Création de la base (optionnel)
-- ============================================
CREATE DATABASE plateforme_services;
\c plateforme_services;

-- ============================================
-- Création des types ENUM PostgreSQL
-- ============================================
CREATE TYPE role_type AS ENUM ('client', 'prestataire', 'admin');
CREATE TYPE type_prix AS ENUM ('fixe', 'horaire');
CREATE TYPE statut_demande AS ENUM ('en_attente', 'acceptee', 'refusee', 'terminee');
CREATE TYPE statut_paiement AS ENUM ('en_attente', 'paye', 'echec');
CREATE TYPE type_message AS ENUM ('texte', 'image', 'system');

-- ============================================
-- TABLE : Utilisateur
-- ============================================
CREATE TABLE Utilisateur (
    id SERIAL PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    prenom VARCHAR(100),
    nom VARCHAR(100),
    role role_type NOT NULL,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    id_specialite int,
    experience TEXT,
    evaluation FLOAT DEFAULT 0,
    verification BOOLEAN DEFAULT FALSE
);

CREATE TABLE specialites (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) UNIQUE NOT NULL
);

INSERT INTO Utilisateur 
(email,
 mot_de_passe,
  prenom,
   nom,
    role,
     telephone,
      adresse,
       id_specialite,
        experience, evaluation,
         verification) VALUES
('alice@example.com', 'password123', 'Alice', 'Dupont', 'prestataire', '0341234567', 'Antananarivo', 1, '5 ans dexperience en developpement logiciel', 4.5, TRUE);

INSERT INTO specialites (nom) VALUES
('Plomberie'),
('Electricite'),
('Menuiserie'),
('Mecanique'),
('Informatique'),
('Cuisine'),
('Jardinage'),
('Peinture'),
('Coiffure');


-- ============================================
-- TABLE : Services
-- ============================================
CREATE TABLE Services (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    categorie VARCHAR(100),
    sous_categorie VARCHAR(100),
    prix NUMERIC(10,2),
    type_de_prix type_prix,
    duree VARCHAR(50),
    id_prestataire INT,
    disponible BOOLEAN DEFAULT TRUE,
    featured BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (id_prestataire) REFERENCES Utilisateur(id)
        ON DELETE SET NULL
);

-- ============================================
-- TABLE : Demande
-- ============================================
CREATE TABLE Demande (
    id SERIAL PRIMARY KEY,
    serviceId INT,
    clientId INT,
    prestataireId INT,
    titre VARCHAR(255),
    description TEXT,
    statut statut_demande DEFAULT 'en_attente',
    date_preferee DATE,
    adresse VARCHAR(255),
    budget NUMERIC(10,2),
    urgence BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (serviceId) REFERENCES Services(id) ON DELETE SET NULL,
    FOREIGN KEY (clientId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (prestataireId) REFERENCES Utilisateur(id) ON DELETE SET NULL
);

-- ============================================
-- TABLE : Paiements
-- ============================================
CREATE TABLE Paiements (
    id SERIAL PRIMARY KEY,
    demandeId INT,
    clientId INT,
    prestataireId INT,
    servicesId INT,
    montant NUMERIC(10,2),
    devise VARCHAR(10),
    statut statut_paiement DEFAULT 'en_attente',
    method_de_paiement VARCHAR(50),
    transactionId VARCHAR(255),

    FOREIGN KEY (demandeId) REFERENCES Demande(id) ON DELETE CASCADE,
    FOREIGN KEY (clientId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (prestataireId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (servicesId) REFERENCES Services(id) ON DELETE SET NULL
);

-- ============================================
-- TABLE : Messages
-- ============================================
CREATE TABLE Messages (
    id SERIAL PRIMARY KEY,
    demandeId INT,
    expediteurId INT,
    destinataireId INT,
    contenu TEXT,
    type type_message,
    lu BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (demandeId) REFERENCES Demande(id) ON DELETE CASCADE,
    FOREIGN KEY (expediteurId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (destinataireId) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE : Notation
-- ============================================
CREATE TABLE Notation (
    id SERIAL PRIMARY KEY,
    demandeId INT,
    clientId INT,
    prestataireId INT,
    note INT CHECK (note >= 1 AND note <= 5),
    commentaire TEXT,

    FOREIGN KEY (demandeId) REFERENCES Demande(id) ON DELETE CASCADE,
    FOREIGN KEY (clientId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (prestataireId) REFERENCES Utilisateur(id) ON DELETE CASCADE
);


-----------------------------------------------------------------------------------

-- ================================
-- UTILISATEURS : Clients & Prestataires
-- ================================
INSERT INTO Utilisateur 
(email, mot_de_passe, prenom, nom, role, telephone, adresse, specialite, experience, evaluation, verification)
VALUES
('client1@example.com', 'pass123', 'Marc', 'Dupont', 'client', '0340000001', 'Antananarivo', NULL, NULL, 0, TRUE),
('client2@example.com', 'pass123', 'Sarah', 'Randria', 'client', '0340000002', 'Tamatave', NULL, NULL, 0, FALSE),
('prest1@example.com', 'pass123', 'Jean', 'Rakoto', 'prestataire', '0340001001', 'Antsirabe', 'Plomberie', '10 ans d’expérience', 4.5, TRUE),
('prest2@example.com', 'pass123', 'Aina', 'Rasoa', 'prestataire', '0340001002', 'Fianarantsoa', 'Électricité', '7 ans d’expérience', 4.2, TRUE),
('admin@example.com', 'adminpass', 'Admin', 'System', 'admin', '0349999999', 'Antananarivo', NULL, NULL, 0, TRUE);

-- ================================
-- SERVICES
-- ================================
INSERT INTO Services (titre, description, categorie, sous_categorie, prix, type_de_prix, duree, id_prestataire, disponible, featured)
VALUES
('Réparation fuite d’eau', 'Réparation rapide des fuites', 'Maison', 'Plomberie', 50000, 'fixe', '1h', 3, TRUE, FALSE),
('Installation prise électrique', 'Installation conforme des prises', 'Maison', 'Électricité', 40000, 'fixe', '45 min', 4, TRUE, TRUE),
('Débouchage évier', 'Service professionnel de débouchage', 'Maison', 'Plomberie', 30000, 'fixe', '30 min', 3, TRUE, FALSE);

-- ================================
-- DEMANDES
-- ================================
INSERT INTO Demande (serviceId, clientId, prestataireId, titre, description, statut, date_preferee, adresse, budget, urgence)
VALUES
(1, 1, 3, 'Fuite dans la cuisine', 'Grosse fuite sous l’évier', 'en_attente', '2025-01-10', 'Antananarivo centre', 60000, TRUE),
(2, 2, 4, 'Nouvelle prise', 'Besoin d’une nouvelle prise dans le salon', 'acceptee', '2025-01-12', 'Tamatave nord', 45000, FALSE),
(3, 1, 3, 'Évier bouché', 'Évacuation lente', 'terminee', '2024-12-28', 'Antananarivo centre', 35000, FALSE);

-- ================================
-- MESSAGES (clients ↔ prestataires)
-- ================================
INSERT INTO Messages (demandeId, expediteurId, destinataireId, contenu, type, lu)
VALUES
(1, 1, 3, 'Bonjour, pouvez-vous venir rapidement ?', 'texte', FALSE),
(1, 3, 1, 'Oui, je peux passer dans 1h.', 'texte', TRUE),
(2, 2, 4, 'Quand serez-vous disponible ?', 'texte', FALSE),
(3, 3, 1, 'Travail terminé, merci.', 'texte', TRUE);

-- ================================
-- PAIEMENTS
-- ================================
INSERT INTO Paiements (demandeId, clientId, prestataireId, servicesId, montant, devise, statut, method_de_paiement, transactionId)
VALUES
(1, 1, 3, 1, 60000, 'MGA', 'en_attente', 'mobile_money', 'TXN001'),
(2, 2, 4, 2, 45000, 'MGA', 'paye', 'mobile_money', 'TXN002'),
(3, 1, 3, 3, 35000, 'MGA', 'paye', 'carte', 'TXN003');

-- ================================
-- NOTATIONS
-- ================================
INSERT INTO Notation (demandeId, clientId, prestataireId, note, commentaire)
VALUES
(3, 1, 3, 5, 'Service rapide et efficace !'),
(2, 2, 4, 4, 'Bon travail, mais petit retard.');
